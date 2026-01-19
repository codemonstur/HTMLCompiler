package htmlcompiler.minify;

import com.googlecode.htmlcompressor.compressor.ClosureJavaScriptCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import htmlcompiler.utils.Logger;
import net.logicsquad.minifier.MinificationException;
import net.logicsquad.minifier.js.JSMinifier;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static com.google.javascript.jscomp.CompilationLevel.*;
import static com.google.javascript.jscomp.CompilationLevel.ADVANCED_OPTIMIZATIONS;

public enum JsMinifyEngine {

    gcc_simple, gcc_bundle, gcc_whitespace, gcc_advanced, yui, logicsquad;

    public Minifier toMinifier(final Logger log) {
        return switch (this) {
            case gcc_simple -> JsMinifyEngine::compressJsWithGccSimple;
            case gcc_bundle -> JsMinifyEngine::compressJsWithGccBundle;
            case gcc_whitespace -> JsMinifyEngine::compressJsWithGccWhitespace;
            case gcc_advanced -> JsMinifyEngine::compressJsWithGccAdvanced;
            case yui -> JsMinifyEngine.newCompressJsWithYui(log);
            case logicsquad -> JsMinifyEngine::minifyJsWithLogicSquad;
        };
    }

    public static String compressJsWithGccSimple(final String code) {
        final var compress = new ClosureJavaScriptCompressor(SIMPLE_OPTIMIZATIONS);
        return compress.compress(code);
    }
    public static String compressJsWithGccWhitespace(final String code) {
        final var compress = new ClosureJavaScriptCompressor(WHITESPACE_ONLY);
        return compress.compress(code);
    }
    public static String compressJsWithGccBundle(final String code) {
        final var compress = new ClosureJavaScriptCompressor(BUNDLE);
        return compress.compress(code);
    }
    public static String compressJsWithGccAdvanced(final String code) {
        final var compress = new ClosureJavaScriptCompressor(ADVANCED_OPTIMIZATIONS);
        return compress.compress(code);
    }

    public static Minifier newCompressJsWithYui(final Logger log) {
        return code -> {
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
        };
    }

    public static String minifyJsWithLogicSquad(final String code) {
        final var min = new JSMinifier(new StringReader(code));
        try {
            final var out = new StringWriter();
            min.minify(out);
            return out.toString();
        } catch (final MinificationException e) {
            throw new RuntimeException(e);
        }
    }

}
