package htmlcompiler.compilers;

import htmlcompiler.minify.Minifier;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.error.InvalidTemplate;
import htmlcompiler.utils.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static htmlcompiler.compilers.CodeCompiler.newNopCompiler;
import static htmlcompiler.compilers.CssCompiler.*;
import static htmlcompiler.compilers.JsCompiler.*;
import static htmlcompiler.compilers.TemplateEngines.*;
import static java.util.Map.entry;

public interface FileCompiler {

    String compile(Path file) throws IOException, InvalidTemplate, InvalidInput;
    String outputExtension();

    private static FileCompiler newHtmlCompiler(final HtmlCompiler html, final HtmlTemplateEngine engine) {
        return new FileCompiler() {
            public String compile(final Path file) throws IOException, InvalidTemplate, InvalidInput {
                return html.doctypeCompressCompile(file, engine.compile(file));
            }
            public String outputExtension() {
                return engine.outputExtension();
            }
        };
    }
    private static FileCompiler newScriptCompiler(final Minifier compress, final CodeCompiler compiler
            , final String extension) {
        return new FileCompiler() {
            public String compile(final Path file) throws InvalidTemplate {
                try {
                    return compress.minify(compiler.compileCode(file));
                } catch (final Exception e) {
                    throw new InvalidTemplate(e);
                }
            }
            public String outputExtension() {
                return extension;
            }
        };
    }

    public static Map<String, FileCompiler> newFileCompilerMap(final Logger logger
            , final String baseDir, final HtmlCompiler html, final Map<String, String> context) {
        // The compressors honour the configured compression flags, so standalone
        // assets follow --no-compression like inline code does
        return Map.ofEntries
            ( entry(".pebble", newHtmlCompiler(html, newPebbleEngine(context)))
            , entry(".jade", newHtmlCompiler(html, newJade4jEngine(context)))
            , entry(".pug", newHtmlCompiler(html, newPug4jEngine(baseDir, context)))
            , entry(".htm", newHtmlCompiler(html, Files::readString))
            , entry(".html", newHtmlCompiler(html, Files::readString))
            , entry(".hct", newHtmlCompiler(html, Files::readString))
            , entry(".css", newScriptCompiler(html::compressCss, newNopCompiler(), ".min.css"))
            , entry(".scss", newScriptCompiler(html::compressCss, newScssCompiler(logger), ".min.css"))
            , entry(".stylus", newScriptCompiler(html::compressCss, newStylusCompiler(), ".min.css"))
            , entry(".js", newScriptCompiler(html::compressJs, newNopCompiler(), ".min.js"))
            , entry(".jspp", newScriptCompiler(html::compressJs, newJsppCompiler(), ".min.js"))
            , entry(".js++", newScriptCompiler(html::compressJs, newJsppCompiler(), ".min.js"))
            , entry(".dart", newScriptCompiler(html::compressJs, newDartCompiler(), ".min.js"))
            , entry(".ts", newScriptCompiler(html::compressJs, newTypescriptCompiler(), ".min.js"))
            );
    }

}
