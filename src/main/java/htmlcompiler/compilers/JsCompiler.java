package htmlcompiler.compilers;

import com.googlecode.htmlcompressor.compressor.ClosureJavaScriptCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import htmlcompiler.tools.Logger;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static com.google.javascript.jscomp.CompilationLevel.*;
import static htmlcompiler.compilers.CodeCompiler.newExternalToolCompiler;

public enum JsCompiler {;

    public static Compressor newJsCompressor(final Logger log, final String type) {
        return switch (type) {
            case "gcc-simple" -> JsCompiler::compressJsWithGccSimple;
            case "gcc-bundle" -> JsCompiler::compressJsWithGccBundle;
            case "gcc-whitespace" -> JsCompiler::compressJsWithGccWhitespace;
            case "gcc-advanced" -> JsCompiler::compressJsWithGccAdvanced;
            case "yui" -> JsCompiler.newCompressJsWithYui(log);
            default -> throw new IllegalArgumentException("No such compressor: " + type);
        };
    }

    private static String compressJsWithGccSimple(final String code) {
        final var compress = new ClosureJavaScriptCompressor(SIMPLE_OPTIMIZATIONS);
        return compress.compress(code);
    }
    private static String compressJsWithGccWhitespace(final String code) {
        final var compress = new ClosureJavaScriptCompressor(WHITESPACE_ONLY);
        return compress.compress(code);
    }
    private static String compressJsWithGccBundle(final String code) {
        final var compress = new ClosureJavaScriptCompressor(BUNDLE);
        return compress.compress(code);
    }
    private static String compressJsWithGccAdvanced(final String code) {
        final var compress = new ClosureJavaScriptCompressor(ADVANCED_OPTIMIZATIONS);
        return compress.compress(code);
    }

    private static Compressor newCompressJsWithYui(final Logger log) {
        return new Compressor() {
            @Override public String compress(final String code) {
                try {
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
                } catch (final IOException e) {
                    throw new IllegalStateException("IOException on internal StringWriter", e);
                }
            }
        };
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
