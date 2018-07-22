package htmlcompiler.model;

import htmlcompiler.model.error.UnrecognizedFileType;

import java.io.File;

public enum ScriptType {
    minified_javascript, extended_javascript, javascript, typescript;

    public static ScriptType toJavascriptType(final File file) throws UnrecognizedFileType {
        return toJavascriptType(file.getName());
    }
    public static ScriptType toJavascriptType(final String filename) throws UnrecognizedFileType {
        if (filename.endsWith(".min.js")) return minified_javascript;
        if (filename.endsWith(".ejs")) return extended_javascript;
        if (filename.endsWith(".js")) return javascript;
        if (filename.endsWith(".ts")) return typescript;
        throw new UnrecognizedFileType(filename);
    }
}
