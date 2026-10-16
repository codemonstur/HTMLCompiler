package htmlcompiler.services;

import com.google.gson.reflect.TypeToken;
import htmlcompiler.pojos.versions.CdnJsResponse;
import htmlcompiler.pojos.versions.Version;
import htmlcompiler.utils.HTTP;
import htmlcompiler.utils.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static htmlcompiler.utils.Filenames.toRelativePath;
import static htmlcompiler.utils.Json.GSON;
import static java.nio.file.Files.*;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static xmlparser.utils.Functions.isNullOrEmpty;

public enum RepositoryVersions {;

    private static final Pattern IS_CDNJS = Pattern.compile("https?:\\/\\/cdnjs\\.cloudflare\\.com\\/ajax\\/libs\\/([^\\/]+)\\/([^\\/]+)\\/.*");

    public static void checkVersionLibrary(final Logger log, final String fileName, final String url
            , final boolean ignoreMajor) {
        final Matcher matcher = IS_CDNJS.matcher(url);
        if (!matcher.find()) return;
        try {
            final var libraryName = matcher.group(1);
            final var libraryVersion = new Version(matcher.group(2));
            final var allVersions = listVersions(libraryName);
            final var message = toVersionMessage
                ( ignoreMajor ? Optional.empty() : findNewerMajor(libraryVersion, allVersions)
                , findNewerMinor(libraryVersion, allVersions)
                , findNewerPatch(libraryVersion, allVersions)
                );

            if (!isNullOrEmpty(message)) {
                log.warn(String.format("File %s uses outdated library %s:%s; %s", toRelativePath(fileName), libraryName, libraryVersion.original(), message));
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (final IOException | RuntimeException e) {
            // Version checking is advisory: a network or parsing problem must not fail the build
            log.warn("Failed to check the version of library " + url + ": " + e.getMessage());
        }
    }

    private static String toVersionMessage(final Optional<Version> major, final Optional<Version> minor, final Optional<Version> patch) {
        final var parts = new ArrayList<String>();
        major.ifPresent(version -> parts.add("latest major: " + version.original()));
        minor.ifPresent(version -> parts.add("latest minor: " + version.original()));
        patch.ifPresent(version -> parts.add("latest patch: " + version.original()));
        return String.join(", ", parts);
    }

    private static Optional<Version> findNewerMajor(final Version current, final List<Version> allVersions) {
        return allVersions.stream()
            .filter(available -> available.major() > current.major())
            .reduce((first, second) -> first.isNewerThan(second) ? first : second);
    }
    private static Optional<Version> findNewerMinor(final Version current, final List<Version> allVersions) {
        return allVersions.stream()
            .filter(available -> available.major() == current.major())
            .filter(available -> available.minor() > current.minor())
            .reduce((first, second) -> first.isNewerThan(second) ? first : second);
    }
    private static Optional<Version> findNewerPatch(final Version current, final List<Version> allVersions) {
        return allVersions.stream()
            .filter(available -> available.major() == current.major())
            .filter(available -> available.minor() == current.minor())
            .filter(available -> available.patch() > current.patch())
            .reduce((first, second) -> first.isNewerThan(second) ? first : second);
    }

    private static final Type TYPE_LIST_VERSIONS = new TypeToken<List<Version>>(){}.getType();

    private static List<Version> listVersions(final String library) throws IOException, InterruptedException {
        final Path cache = getVersionRepositoryPath().resolve(library + ".json");
        if (isCachedVersionFile(cache)) {
            try {
                final List<Version> cachedVersions = GSON.fromJson(readString(cache), TYPE_LIST_VERSIONS);
                if (cachedVersions != null) return cachedVersions;
            } catch (final RuntimeException e) {
                // Corrupt cache file: delete it and download the list again
            }
            Files.deleteIfExists(cache);
        }

        final var list = downloadListVersions(library);
        createDirectories(cache.getParent());
        writeString(cache, GSON.toJson(list), CREATE, TRUNCATE_EXISTING);
        return list;
    }

    private static Path getVersionRepositoryPath() throws IOException {
        return Repository.getRepositoryDirectory().resolve("versions");
    }

    private static boolean isCachedVersionFile(final Path file) throws IOException {
        return isRegularFile(file) && Files.getLastModifiedTime(file).toMillis() > oneDayAgo();
    }

    private static long oneDayAgo() {
        return System.currentTimeMillis() - DAYS.toMillis(1);
    }

    private static final String URI_CDN_JS_VERSIONS = "https://api.cdnjs.com/libraries/%s?fields=versions";
    private static List<Version> downloadListVersions(final String library) throws IOException, InterruptedException {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URI_CDN_JS_VERSIONS, library)))
                .timeout(Duration.ofSeconds(30))
                .build();
        final var response = HTTP.HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() > 299)
            throw new IOException("api.cdnjs.com returned HTTP " + response.statusCode() + " for library " + library);
        final CdnJsResponse parsed = GSON.fromJson(response.body(), CdnJsResponse.class);
        if (parsed == null || parsed.versions() == null)
            throw new IOException("Unexpected response from api.cdnjs.com for library " + library);
        return parsed.versions().stream().map(Version::new).collect(toList());
    }

}
