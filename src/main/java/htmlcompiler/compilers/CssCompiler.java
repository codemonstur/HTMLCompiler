package htmlcompiler.compilers;

import com.inet.lib.less.Less;
import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.handler.SCSSErrorHandler;
import com.vaadin.sass.internal.parser.Parser;
import com.vaadin.sass.internal.resolver.ClassloaderResolver;
import com.vaadin.sass.internal.resolver.FilesystemResolver;
import com.yahoo.platform.yui.compressor.CssCompressor;
import htmlcompiler.utils.Logger;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.InputSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.compilers.CodeCompiler.newExternalToolCompiler;
import static htmlcompiler.compilers.CodeCompiler.newStdoutToolCompiler;
import static htmlcompiler.utils.Logger.newVaadinLogger;

public enum CssCompiler {;

    public static CodeCompiler newStylusCompiler() {
        // The stylus -o option takes a directory, so the compiled CSS is printed to stdout instead
        return newStdoutToolCompiler("stylus", ".styl",
            inputFile -> new String[]{"--print", inputFile.toAbsolutePath().toString()});
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
            (outputFile, inputFile) -> new String[]{"--no-source-map", inputFile.toAbsolutePath().toString(), outputFile.toAbsolutePath().toString()});
    }

    public static CodeCompiler newInternalScssCompiler(final Logger logger) {
        final var errorHandler = newVaadinLogger(logger);
        return new CodeCompiler() {
            public String compileCode(final String code, final Path parent) throws Exception {
                return toCssCode(toCompiledStylesheet(code, toBackingFile(parent)));
            }
            public String compileCode(final Path style) throws Exception {
                return toCssCode(toCompiledStylesheet(Files.readString(style), style.toFile()));
            }
            private File toBackingFile(final Path parent) {
                if (parent == null) return null;
                final Path dir = Files.isDirectory(parent) ? parent : parent.getParent();
                return dir == null ? null : dir.resolve("__inline__.scss").toFile();
            }
            private ScssStylesheet toCompiledStylesheet(final String code, final File backingFile) throws Exception {
                final SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();

                // The thread local error handler must be set before compiling, otherwise
                // import failures inside vaadin NPE while trying to report themselves
                SCSSErrorHandler.set(newVaadinTraversalLogger(logger));
                final var scssCompiler = new Parser();
                scssCompiler.setErrorHandler(errorHandler);
                scssCompiler.setDocumentHandler(handler);

                // Any failure from here on must propagate: the vaadin error handlers rethrow,
                // because a logged-and-returned parse error would emit a partial stylesheet
                // while the build still succeeds
                scssCompiler.parseStyleSheet(new InputSource(new StringReader(code)));

                // Without resolvers, a backing file, and a charset the parsed sheet
                // cannot resolve @import statements and fails on any stylesheet that uses them
                final ScssStylesheet stylesheet = handler.getStyleSheet();
                stylesheet.addResolver(new FilesystemResolver());
                stylesheet.addResolver(new ClassloaderResolver());
                if (backingFile != null) stylesheet.setFile(backingFile);
                stylesheet.setCharset("ASCII");
                stylesheet.compile();

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

    private static SCSSErrorHandler newVaadinTraversalLogger(final Logger logger) {
        return new SCSSErrorHandler() {
            // Every handler rethrows: vaadin reports recoverable errors by calling these
            // and continuing, which would silently drop imports or emit a partial sheet.
            // traverseError(String) is caught by vaadin and re-reported through
            // traverseError(Exception), which is not caught again, so this propagates
            @Override
            public void traverseError(final Exception e) {
                logger.error(e.getMessage() == null ? e.toString() : e.getMessage());
                throw e instanceof RuntimeException runtime ? runtime : new IllegalStateException(e);
            }
            @Override
            public void traverseError(final String message) {
                logger.error(message);
                throw new IllegalStateException(message);
            }
            // This handler is also used as the parser for imported sheets
            @Override
            public void error(final CSSParseException e) throws CSSException {
                logger.error(e.getMessage());
                throw e;
            }
            @Override
            public void fatalError(final CSSParseException e) throws CSSException {
                logger.error(e.getMessage());
                throw e;
            }
            @Override
            public void warning(final CSSParseException e) {
                logger.warn(e.getMessage());
            }
        };
    }

}
