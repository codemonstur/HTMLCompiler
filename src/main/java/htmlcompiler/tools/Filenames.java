package htmlcompiler.tools;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.isRegularFile;

public enum Filenames {;

    public static String toExtension(final String filename) {
        return filename.substring(filename.lastIndexOf('.')+1);
    }
    public static String toExtension(final Path path) {
        final String filename = path.getFileName().toString();
        return filename.substring(filename.lastIndexOf('.')+1);
    }

    public static String toExtension(final Path file, final String _default) {
        if (file == null || !isRegularFile(file)) return _default;
        final String name = file.getFileName().toString();
        if (name.isEmpty()) return _default;
        final int index = name.lastIndexOf('.');
        if (index == -1) return _default;
        return name.substring(index);
    }

    private static final Path CURRENT_WORKING_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    public static String toRelativePath(final String fileName) {
        return toRelativePath(Paths.get(fileName));
    }
    public static String toRelativePath(final Path path) {
        if (!path.isAbsolute()) return path.toString();
        return CURRENT_WORKING_DIRECTORY.relativize(path).toString();
    }

}
