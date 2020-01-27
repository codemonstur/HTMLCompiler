package htmlcompiler.compilers.scripts;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static htmlcompiler.compilers.scripts.CodeCompiler.newExternalToolCompiler;

public enum JsCompiler {;

    public static String compressJavascriptCode(final String code) throws IOException {
        final JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(code), null);
        final StringWriter writer = new StringWriter();
        compressor.compress(writer, -1, true, false, false, false);
        return writer.toString();
    }

    public static CodeCompiler newTypescriptCompiler() {
        return newExternalToolCompiler("tsc", ".tsc",
            (outputFile, inputFile) -> "--outFile " + outputFile.toAbsolutePath() + " " + inputFile.toAbsolutePath());
    }

    public static CodeCompiler newJsppCompiler() {
        return newExternalToolCompiler("js++", ".jspp",
            (outputFile, inputFile) -> inputFile.toAbsolutePath() + " -o " + outputFile.toAbsolutePath());
    }

    public static CodeCompiler newDartCompiler() {
        return newExternalToolCompiler("dart2js", ".dart",
            (outputFile, inputFile) -> "-o " + outputFile.toAbsolutePath() + " " + inputFile.toAbsolutePath());
    }

}
