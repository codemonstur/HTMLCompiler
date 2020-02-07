package htmlcompiler.tools;

import java.nio.file.Path;

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
}
