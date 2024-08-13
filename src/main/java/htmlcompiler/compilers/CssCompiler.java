package htmlcompiler.compilers;

import com.inet.lib.less.Less;
import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.parser.Parser;
import com.yahoo.platform.yui.compressor.CssCompressor;
import org.w3c.css.sac.InputSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.compilers.CodeCompiler.newExternalToolCompiler;

public enum CssCompiler {;

    public static String compressCssCode(final String code) {
        try (final var reader = new StringReader(code); final var writer = new StringWriter()) {
            final CssCompressor compressor = new CssCompressor(reader);
            compressor.compress(writer, -1);
            return writer.toString();
        } catch (final IOException e) {
            throw new IllegalStateException("IOException on StringReader or StringWriter", e);
        }
    }

    public static CodeCompiler newStylusCompiler() {
        return newExternalToolCompiler("stylus", ".styl", true, ".css",
            (outputFile, inputFile) -> inputFile.toAbsolutePath().toString() + " -o " + outputFile.toAbsolutePath().toString());
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

    public static CodeCompiler newScssCompiler() {
        final CodeCompiler tool = newToolScssCompiler();
        final CodeCompiler lib = newInternalSassCompiler();
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

    public static CodeCompiler newSassCompiler() {
        final CodeCompiler tool = newToolSassCompiler();
        final CodeCompiler lib = newInternalSassCompiler();
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
            (outputFile, inputFile) -> "--no-source-map " + inputFile.toAbsolutePath().toString() + " " + outputFile.toAbsolutePath().toString());
    }

    public static CodeCompiler newToolSassCompiler() {
        return newExternalToolCompiler("sass", ".sass", true, ".css",
            (outputFile, inputFile) -> "--no-source-map " + inputFile.toAbsolutePath().toString() + " " + outputFile.toAbsolutePath().toString());
    }

    public static CodeCompiler newInternalSassCompiler() {
        final Parser sassCompiler = new Parser();
        return new CodeCompiler() {
            public String compileCode(final String code, final Path parent) throws Exception {
                return toCssCode(toCompiledStylesheet(sassCompiler, code));
            }
            public String compileCode(final Path style) throws Exception {
                return toCssCode(toCompiledStylesheet(sassCompiler, Files.readString(style)));
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
