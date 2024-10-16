package htmlcompiler.compilers;

import com.inet.lib.less.Less;
import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.parser.Parser;
import com.yahoo.platform.yui.compressor.CssCompressor;
import htmlcompiler.utils.Logger;
import org.w3c.css.sac.InputSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.compilers.CodeCompiler.newExternalToolCompiler;
import static htmlcompiler.utils.Logger.newVaadinLogger;

public enum CssCompiler {;

    public static CodeCompiler newStylusCompiler() {
        return newExternalToolCompiler("stylus", ".styl", true, ".css",
            (outputFile, inputFile) -> inputFile.toAbsolutePath().toString() + " -o " + outputFile.toAbsolutePath());
    }

    public static CodeCompiler newLessCompiler() {
        return new CodeCompiler() {
            public String compileCode(final String code, final Path parent) {
                return Less.compile(null, code, false);
            }
            public String compileCode(final Path style) throws IOException {
                return Less.compile(null, Files.readString(style), false);
            }
        };
    }

    public static CodeCompiler newScssCompiler(final Logger logger) {
        final var tool = newToolScssCompiler();
        final var lib = newInternalScssCompiler(logger);
        return new CodeCompiler() {
            public String compileCode(final String code, final Path parent) throws Exception {
                try {
                    return tool.compileCode(code, parent);
                } catch (FileNotFoundException e) {
                    return lib.compileCode(code, parent);
                }
            }
            public String compileCode(final Path style) throws Exception {
                try {
                    return tool.compileCode(style);
                } catch (FileNotFoundException e) {
                    return lib.compileCode(style);
                }
            }
        };
    }

    public static CodeCompiler newToolScssCompiler() {
        return newExternalToolCompiler("sass", ".scss", true, ".css",
            (outputFile, inputFile) -> "--no-source-map " + inputFile.toAbsolutePath() + " " + outputFile.toAbsolutePath());
    }

    public static CodeCompiler newInternalScssCompiler(final Logger logger) {
        final var errorHandler = newVaadinLogger(logger);
        return new CodeCompiler() {
            public String compileCode(final String code, final Path parent) throws Exception {
                return toCssCode(toCompiledStylesheet(code));
            }
            public String compileCode(final Path style) throws Exception {
                return toCssCode(toCompiledStylesheet(Files.readString(style)));
            }
            private ScssStylesheet toCompiledStylesheet(final String code) {
                final SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();

                final var scssCompiler = new Parser();
                scssCompiler.setErrorHandler(errorHandler);
                scssCompiler.setDocumentHandler(handler);
                try {
                    scssCompiler.parseStyleSheet(new InputSource(new StringReader(code)));
                    handler.getStyleSheet().setCharset("ASCII");
                    handler.getStyleSheet().compile();
                } catch (final Exception e) {
                    logger.error(e.getMessage());
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
