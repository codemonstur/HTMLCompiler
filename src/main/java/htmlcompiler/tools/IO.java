package htmlcompiler.tools;

import htmlcompiler.pojos.error.InvalidInput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
}
