package htmlcompiler.tools;

import java.io.File;

public enum Filenames {;

    public static String toExtension(final String filename) {
        return filename.substring(filename.lastIndexOf('.')+1);
    }

    public static String toExtension(final File file, final String _default) {
        if (file == null || !file.isFile()) return _default;
        final String name = file.getName();
        if (name.isEmpty()) return _default;
        final int index = name.lastIndexOf('.');
        if (index == -1) return _default;
        return name.substring(index);
    }
}
