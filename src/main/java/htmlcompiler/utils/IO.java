package htmlcompiler.utils;

import htmlcompiler.pojos.error.InvalidInput;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public enum IO {;

    public static Path toLocation(final Path origin, final String link, final String message) throws InvalidInput {
        final Path location = (isDirectory(origin) ? origin : origin.getParent()).resolve(link);
        if (!Files.exists(location)) throw new InvalidInput(format(message, origin, link));
        return location;
    }

    public static Path findBinaryInPath(final String name) throws FileNotFoundException {
        final Optional<Path> location = findBinary(name);
        if (location.isEmpty()) throw new FileNotFoundException("Could not find binary " + name + " in PATH");
        return location.get();
    }

    public static Path findBinaryInPath(final String name, final Path defaultValue) {
        return findBinary(name).orElse(defaultValue);
    }

    private static Optional<Path> findBinary(final String name) {
        return Stream.of(System.getenv("PATH")
            .split(Pattern.quote(File.pathSeparator)))
            .map(Paths::get)
            .map(directory -> resolveBinary(directory, name))
            .filter(Objects::nonNull)
            .findAny();
    }

    private static Path resolveBinary(final Path directory, final String name) {
        final Path direct = directory.resolve(name);
        if (Files.isRegularFile(direct)) return direct;
        if (isWindows()) {
            final String pathExt = System.getenv().getOrDefault("PATHEXT", ".COM;.EXE;.BAT;.CMD");
            for (final String extension : pathExt.split(";")) {
                final Path candidate = directory.resolve(name + extension.toLowerCase(Locale.ROOT));
                if (Files.isRegularFile(candidate)) return candidate;
            }
        }
        return null;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    public static Path newTempFileWithContent(final String prefix, final String suffix, final Path tempDir, final String content) throws IOException {
        final Path tempFile = createTempFile(tempDir, prefix, suffix);
        try {
            Files.writeString(tempFile, content, CREATE, TRUNCATE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            Files.delete(tempFile);
            throw e;
        }
    }

    public static String loadResource(final String resourcePath, final Charset charset) throws IOException {
        try (final var in = IO.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new FileNotFoundException("Resource " + resourcePath + " does not exist.");
            return streamAsString(in, charset);
        }
    }

    private static String streamAsString(final InputStream in, final Charset charset) throws IOException {
        try (final var result = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            for (int read; (read = in.read(buffer)) != -1;) {
                result.write(buffer, 0, read);
            }
            return result.toString(charset);
        }
    }

}
