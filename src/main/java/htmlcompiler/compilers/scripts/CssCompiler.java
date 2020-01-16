package htmlcompiler.compilers.scripts;

import com.inet.lib.less.Less;
import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.parser.Parser;
import com.yahoo.platform.yui.compressor.CssCompressor;
import htmlcompiler.tools.IO;
import org.w3c.css.sac.InputSource;

import java.io.*;

import static htmlcompiler.compilers.scripts.CodeCompiler.newExternalToolCompiler;

public enum CssCompiler {;

    public static String compressCssCode(final String code) throws IOException {
        final CssCompressor compressor = new CssCompressor(new StringReader(code));
        final StringWriter writer = new StringWriter();
        compressor.compress(writer, -1);
        return writer.toString();
    }

    public static CodeCompiler newStylusCompiler() {
        return newExternalToolCompiler("stylus", ".styl", true, ".css",
            (outputFile, inputFile) -> inputFile.getAbsolutePath() + " -o " + outputFile.getAbsolutePath());
    }

    public static CodeCompiler newLessCompiler() {
        return new CodeCompiler() {
            public String compileCode(String code, File parent) {
                return Less.compile(null, code, false);
            }
            public String compileCode(File style) throws IOException {
                return Less.compile(null, IO.toString(style), false);
            }
        };
    }

    public static CodeCompiler newSassCompiler() {
        final Parser sassCompiler = new Parser();
        return new CodeCompiler() {
            public String compileCode(String code, File parent) throws Exception {
                return toCssCode(toCompiledStylesheet(sassCompiler, code));
            }
            public String compileCode(File style) throws Exception {
                return toCssCode(toCompiledStylesheet(sassCompiler, IO.toString(style)));
            }
            private ScssStylesheet toCompiledStylesheet(final Parser sassCompiler, final String code) {
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
            private String toCssCode(final ScssStylesheet stylesheet) throws IOException {
                try (final Writer writer = new StringWriter()) {
                    stylesheet.write(writer, false);
                    return writer.toString();
                }
            }
        };
    }

}
