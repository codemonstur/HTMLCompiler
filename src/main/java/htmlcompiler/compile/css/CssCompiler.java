package htmlcompiler.compile.css;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.parser.Parser;
import com.yahoo.platform.yui.compressor.CssCompressor;
import htmlcompiler.model.StyleType;
import htmlcompiler.error.UnrecognizedFileType;
import htmlcompiler.tools.IO;
import org.lesscss.LessCompiler;
import org.lesscss.LessException;
import org.w3c.css.sac.InputSource;

import java.io.*;

import static htmlcompiler.model.StyleType.toStyleType;

public enum CssCompiler {;

    public static String compressCssCode(final String code) throws IOException {
        final CssCompressor compressor = new CssCompressor(new StringReader(code));
        final StringWriter writer = new StringWriter();
        compressor.compress(writer, -1);
        return writer.toString();
    }

    public static String compileCssFile(final File file) throws IOException, UnrecognizedFileType, LessException {
        return compileCssCode(toStyleType(file), IO.toString(file));
    }
    public static String compileCssCode(final StyleType name, final String code) throws IOException, UnrecognizedFileType, LessException {
        switch (name) {
            case minified_css: return code;
            case css: return code;
            case less: return compileLessCode(code);
            case sass: return compileScssCode(code);
            default: throw new UnrecognizedFileType("");
        }
    }

    private static final LessCompiler lessCompiler = new LessCompiler();
    public static String compileLessFile(final File file) throws LessException, IOException {
        return compileLessCode(IO.toString(file));
    }
    public static String compileLessCode(final String code) throws LessException {
        return lessCompiler.compile(code);
    }

    private static final Parser sassCompiler = new Parser();
    public static String compileScssFile(final File file) throws IOException {
        return compileScssCode(IO.toString(file));
    }
    public static String compileScssCode(final String code) throws IOException {
        return toCssCode(toCompiledStylesheet(code));
    }

    private static ScssStylesheet toCompiledStylesheet(final String code) {
        final SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        sassCompiler.setDocumentHandler(handler);
        try {
            sassCompiler.parseStyleSheet(new InputSource(new StringReader(code)));
            handler.getStyleSheet().setCharset("ASCII");
            handler.getStyleSheet().compile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return handler.getStyleSheet();
    }
    private static String toCssCode(final ScssStylesheet stylesheet) throws IOException {
        try (final Writer writer = new StringWriter()) {
            stylesheet.write(writer, false);
            return writer.toString();
        }
    }
}
