package htmlcompiler.services;

import com.google.gson.reflect.TypeToken;
import htmlcompiler.pojos.versions.CdnJsResponse;
import htmlcompiler.pojos.versions.Version;
import htmlcompiler.tools.HTTP;
import htmlcompiler.tools.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static htmlcompiler.tools.Filenames.toRelativePath;
import static htmlcompiler.tools.Json.GSON;
import static java.nio.file.Files.*;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static xmlparser.utils.Functions.isNullOrEmpty;

public enum RepositoryVersions {;

    private static final Pattern IS_CDNJS = Pattern.compile("https?:\\/\\/cdnjs\\.cloudflare\\.com\\/ajax\\/libs\\/([^\\/]+)\\/([^\\/]+)\\/.*");

    public static void checkVersionLibrary(final Logger log, final String fileName, final String url
            , final boolean ignoreMajor) throws IOException, InterruptedException {
        final Matcher matcher = IS_CDNJS.matcher(url);
        if (matcher.find()) {
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
                    log.warn(String.format("File %s uses outdated library %s:%s; %s", toRelativePath(fileName), libraryName, libraryVersion.original, message));
                }
            } catch (ConnectException e) {
                log.warn("Connect error while downloading version list for library " + url);
            }
        }
    }

    private static String toVersionMessage(final Optional<Version> major, final Optional<Version> minor, final Optional<Version> patch) {
        final var parts = new ArrayList<String>();
        major.ifPresent(version -> parts.add("latest major: " + version.original));
        minor.ifPresent(version -> parts.add("latest minor: " + version.original));
        patch.ifPresent(version -> parts.add("latest patch: " + version.original));
        return String.join(", ", parts);
    }

    private static Optional<Version> findNewerMajor(final Version current, final List<Version> allVersions) {
        return allVersions.stream()
            .filter(available -> available.major > current.major)
            .reduce((first, second) -> first.isNewerThan(second) ? first : second);
    }
    private static Optional<Version> findNewerMinor(final Version current, final List<Version> allVersions) {
        return allVersions.stream()
            .filter(available -> available.major == current.major)
            .filter(available -> available.minor > current.minor)
            .reduce((first, second) -> first.isNewerThan(second) ? first : second);
    }
    private static Optional<Version> findNewerPatch(final Version current, final List<Version> allVersions) {
        return allVersions.stream()
            .filter(available -> available.major == current.major)
            .filter(available -> available.minor == current.minor)
            .filter(available -> available.patch > current.patch)
            .reduce((first, second) -> first.isNewerThan(second) ? first : second);
    }

    private static final Type TYPE_LIST_VERSIONS = new TypeToken<List<Version>>(){}.getType();

    private static List<Version> listVersions(final String library) throws IOException, InterruptedException {
        final Path cache = getVersionRepositoryPath().resolve(library + ".json");
        if (isCachedVersionFile(cache)) return GSON.fromJson(readString(cache), TYPE_LIST_VERSIONS);

        final var list = downloadListVersions(library);
        createDirectories(cache.getParent());
        writeString(cache, GSON.toJson(list), CREATE);
        return list;
    }

    private static Path getVersionRepositoryPath() throws IOException {
        return Repository.getRepositoryDirectory().resolve("versions");
    }

    private static boolean isCachedVersionFile(final Path file) throws IOException {
        return isRegularFile(file) && Files.getLastModifiedTime(file).toMillis() > oneWeekAgo();
    }

    private static long oneWeekAgo() {
        return System.currentTimeMillis() - DAYS.toMillis(1);
    }

    private static final String URI_CDN_JS_VERSIONS = "https://api.cdnjs.com/libraries/%s?fields=versions";
    private static List<Version> downloadListVersions(final String library) throws IOException, InterruptedException {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URI_CDN_JS_VERSIONS, library)))
                .build();
        final var response = HTTP.HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), CdnJsResponse.class).versions
                .stream().map(Version::new).collect(toList());
    }

}
