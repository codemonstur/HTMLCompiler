package htmlcompiler.compilers;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import htmlcompiler.tools.Logger;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static htmlcompiler.compilers.CodeCompiler.newExternalToolCompiler;

public enum JsCompiler {;

    public static String compressJavascriptCode(final Logger log, final String code) throws IOException {
        final JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(code), new ErrorReporter() {
            public void warning(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
                log.warn("In file " + sourceName + " on line " + line + " offset " + lineOffset + ": " + message);
            }
            public void error(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
                log.error("In file " + sourceName + " on line " + line + " offset " + lineOffset + ": " + message);
            }
            public EvaluatorException runtimeError(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
                return new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
            }
        });
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
