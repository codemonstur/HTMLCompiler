package htmlcompiler.compile.js;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import htmlcompiler.model.ScriptType;
import htmlcompiler.error.UnrecognizedFileType;
import htmlcompiler.tools.IO;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static htmlcompiler.compile.js.ExtendedJSCompiler.compileExtendedJavaScript;
import static htmlcompiler.compile.js.TypeScriptCompiler.compileTypeScript;
import static htmlcompiler.model.ScriptType.toJavascriptType;

public enum JsCompiler {;

    public static String compressJavascriptCode(final String code) throws IOException {
        final JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(code), null);
        final StringWriter writer = new StringWriter();
        compressor.compress(writer, -1, true, false, false, false);
        return writer.toString();
    }

    public static String compileJavascriptFile(final File file) throws IOException, UnrecognizedFileType {
        return compileJavascriptCode(toJavascriptType(file), file, IO.toString(file));
    }
    public static String compileJavascriptFile(final ScriptType type, final File file) throws IOException, UnrecognizedFileType {
        return compileJavascriptCode(type, file, IO.toString(file));
    }
    public static String compileJavascriptCode(final ScriptType type, final File file, final String code) throws IOException, UnrecognizedFileType {
        switch (type) {
            case minified_javascript: return code;
            case extended_javascript: return compileExtendedJavaScript(code, file);
            case javascript: return code;
            case typescript: return compileTypeScript(file);
            default: throw new UnrecognizedFileType(file.getName());
        }
    }
}
