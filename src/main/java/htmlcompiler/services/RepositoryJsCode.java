package htmlcompiler.services;

import htmlcompiler.minify.JsMinifyEngine;
import htmlcompiler.minify.Minifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.utils.Coding.encodeHex;
import static htmlcompiler.utils.Coding.sha256;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.concurrent.TimeUnit.DAYS;

public enum RepositoryJsCode {;

    public static String cached(final boolean enabled, final JsMinifyEngine type, final String code, final Minifier minifier) {
        if (!enabled) return minifier.minify(code);

        try {
            final String key = encodeHex(sha256(code, UTF_8));
            final Path path = toFilePath(type.name(), key);
            if (isRegularFile(path) && !isOlderThanOneDay(path)) {
                return Files.readString(path, UTF_8);
            } else {
                final String compressedCode = minifier.minify(code);
                Files.createDirectories(path.getParent());
                Files.writeString(path, compressedCode, UTF_8, CREATE, TRUNCATE_EXISTING);
                return compressedCode;
            }
        } catch (final IOException e) {
            e.printStackTrace();
            return minifier.minify(code);
        }
    }

    private static final long ONE_DAY = DAYS.toMillis(1);
    private static boolean isOlderThanOneDay(final Path path) throws IOException {
        return ONE_DAY < currentTimeMillis() - Files.getLastModifiedTime(path, NOFOLLOW_LINKS).toMillis();
    }

    private static Path toFilePath(final String type, final String key) throws IOException {
        return Repository.getRepositoryDirectory()
            .resolve("js-compress")
            .resolve(type)
            .resolve(key.substring(0, 2))
            .resolve(key.substring(2, 4))
            .resolve(key.substring(4, 6))
            .resolve(key);
    }

}
