package htmlcompiler.minify;

import com.yahoo.platform.yui.compressor.CssCompressor;
import net.logicsquad.minifier.MinificationException;
import net.logicsquad.minifier.css.CSSMinifier;

import java.io.*;

public enum CssMinifyEngine {

    yui, logicsquad;

    public Minifier toMinifier() {
        return switch (this) {
            case yui -> CssMinifyEngine::minifyCssWithYui;
            case logicsquad -> CssMinifyEngine::minifyCssWithLogicSquad;
        };
    }

    public static String minifyCssWithYui(final String code) {
        try (final var reader = new StringReader(code); final var writer = new StringWriter()) {
            final CssCompressor compressor = new CssCompressor(reader);
            compressor.compress(writer, -1);
            return writer.toString();
        } catch (final IOException e) {
            throw new IllegalStateException("IOException on StringReader or StringWriter", e);
        }
    }

    public static String minifyCssWithLogicSquad(final String code) {
        final var min = new CSSMinifier(new StringReader(code));
        try {
            final var out = new StringWriter();
            min.minify(out);
            return out.toString();
        } catch (final MinificationException e) {
            throw new RuntimeException(e);
        }
    }

}
