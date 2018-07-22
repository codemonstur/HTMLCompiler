package htmlcompiler.compiler;

import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.parser.Parser;
import com.yahoo.platform.yui.compressor.CssCompressor;
import htmlcompiler.error.UnrecognizedFileType;
import htmlcompiler.util.Loader;
import org.lesscss.LessCompiler;
import org.lesscss.LessException;
import org.w3c.css.sac.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static java.util.Objects.requireNonNull;

public final class CssCompiler {

    private final Loader loader;
    private final boolean compress;
    public CssCompiler(final Loader loader) {
        this(loader, true);
    }
    public CssCompiler(final Loader loader, final boolean compress) {
        this.loader = requireNonNull(loader, "Loader must not be null");
        this.compress = compress;
    }

    public enum Type {
        MINIFIED_CSS, CSS, LESS, SASS
    }

    public static Type toStyleType(final String location) throws UnrecognizedFileType {
        if (location.endsWith(".min.css")) return Type.MINIFIED_CSS;
        if (location.endsWith(".css")) return Type.CSS;
        if (location.endsWith(".less")) return Type.LESS;
        if (location.endsWith(".scss")) return Type.SASS;
        if (location.endsWith(".sass")) return Type.SASS;
        throw new UnrecognizedFileType(location);
    }

    public String compress(final String content) throws IOException {
        final CssCompressor compressor = new CssCompressor(new StringReader(content));
        final StringWriter writer = new StringWriter();
        compressor.compress(writer, -1);
        return writer.toString();
    }

    public String compile(final String location) throws IOException, UnrecognizedFileType, LessException {
        final String output = compile(toStyleType(location), loader.getAsString(location));
        return compress ? compress(output) : output;
    }

    public String compile(final Type name, final String content) throws IOException, UnrecognizedFileType, LessException {
        String output;
        switch (name) {
            case MINIFIED_CSS: return content;
            case CSS: output = content; break;
            case LESS: output = compileLess(content); break;
            case SASS: output = compileScss(content); break;
            default: throw new UnrecognizedFileType("");
        }
        return compress ? compress(output) : output;
    }

    private static final LessCompiler lessCompiler = new LessCompiler();
    private static String compileLess(final String content) throws LessException {
        return lessCompiler.compile(content);
    }

    private static final Parser sassCompiler = new Parser();
    public static String compileScss(final String content) throws IOException {
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        sassCompiler.setDocumentHandler(handler);
        try {
            sassCompiler.parseStyleSheet(new InputSource(new StringReader(content)));
            handler.getStyleSheet().setCharset("ASCII");
            handler.getStyleSheet().compile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (final Writer writer = new StringWriter()) {
            handler.getStyleSheet().write(writer, false);
            return writer.toString();
        }
    }

}
