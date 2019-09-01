package htmlcompiler.model;

import htmlcompiler.error.UnrecognizedFileType;

import java.io.File;

public enum StyleType {
    minified_css, css, less, sass;

    public static StyleType toStyleType(final File file) throws UnrecognizedFileType {
        return toStyleType(file.getName());
    }

    public static StyleType toStyleType(final String filename) throws UnrecognizedFileType {
        if (filename.endsWith(".min.css")) return minified_css;
        if (filename.endsWith(".css")) return css;
        if (filename.endsWith(".less")) return less;
        if (filename.endsWith(".scss")) return sass;
        if (filename.endsWith(".sass")) return sass;
        throw new UnrecognizedFileType(filename);
    }
}

