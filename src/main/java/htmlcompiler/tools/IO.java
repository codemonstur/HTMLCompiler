package htmlcompiler.tools;

import htmlcompiler.error.InvalidInput;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.io.File.createTempFile;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public enum IO {;

    public static String toString(final File file) throws IOException {
        try (final InputStream in = new FileInputStream(file)) {
            return toString(in, UTF_8);
        }
    }
    public static String toString(final InputStream in) throws IOException {
        return toString(in, UTF_8);
    }
    public static String toString(final InputStream in, final Charset set) throws IOException {
        final StringBuilder out = new StringBuilder();
        byte[] buffer = new byte[1024]; int read;
        while ((read = in.read(buffer)) != -1) {
            out.append(new String(buffer, 0, read, set));
        }
        return out.toString();
    }

    public static byte[] toByteArray(final File file) throws IOException {
        try (final InputStream in = new FileInputStream(file)) {
            return toByteArray(in);
        }
    }
    public static byte[] toByteArray(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    public static String relativize(final String basedir, final String outputDir) {
        return Paths.get(basedir).relativize(Paths.get(outputDir)).toString();
    }

    public static String relativize(final File basedir, final File outputDir) {
        return basedir.toPath().relativize(outputDir.toPath()).toString();
    }

    public static File toLocation(final File origin, final String link, final String message) throws InvalidInput {
        final File location = new File((origin.isDirectory()) ? origin : origin.getParentFile(), link);
        if (!location.exists()) throw new InvalidInput(format(message, origin.getPath(), link));
        return location;
    }

    public static Path findBinaryInPath(final String name) throws FileNotFoundException {
        final Optional<Path> location = Stream.of(System.getenv("PATH")
            .split(Pattern.quote(File.pathSeparator)))
            .map(Paths::get)
            .map(path -> path.resolve(name))
            .filter(path -> Files.exists(path))
            .findAny();
        if (location.isEmpty()) throw new FileNotFoundException("Could not find binary " + name + " in PATH");
        return location.get();
    }

    public static Path findBinaryInPath(final String name, final Path defaultValue) {
        final Optional<Path> location = Stream.of(System.getenv("PATH")
                .split(Pattern.quote(File.pathSeparator)))
                .map(Paths::get)
                .map(path -> path.resolve(name))
                .filter(path -> Files.exists(path))
                .findAny();
        return location.isEmpty() ? defaultValue : location.get();
    }

    public static File newTempFileWithContent(final String prefix, final String suffix, final File tempDir, final String content) throws IOException {
        final File tempFile = createTempFile(prefix, suffix, tempDir);
        try {
            Files.writeString(tempFile.toPath(), content, CREATE, TRUNCATE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            tempFile.delete();
            throw e;
        }
    }
}
