package htmlcompiler.compilers;

import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.error.InvalidTemplate;
import htmlcompiler.tools.Logger;

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
            public String compile(Path file) throws IOException, InvalidTemplate, InvalidInput {
                return html.doctypeCompressCompile(file, engine.compile(file));
            }
            public String outputExtension() {
                return engine.outputExtension();
            }
        };
    }
    private static FileCompiler newScriptCompiler(final Logger logger, final Compressor compressor, final CodeCompiler compiler, final String extension) {
        return new FileCompiler() {
            public String compile(Path file) throws InvalidTemplate {
                try {
                    return compressor.compress(logger, compiler.compileCode(file));
                } catch (Exception e) {
                    throw new InvalidTemplate(e);
                }
            }
            public String outputExtension() {
                return extension;
            }
        };
    }

    public static Map<String, FileCompiler> newFileCompilerMap(final Logger logger, final HtmlCompiler html,
                                                               final Map<String, String> context) {
        return Map.ofEntries
            ( entry(".pebble", newHtmlCompiler(html, newPebbleEngine(context)))
            , entry(".jade", newHtmlCompiler(html, newJade4jEngine(context)))
            , entry(".pug", newHtmlCompiler(html, newPug4jEngine(context)))
            , entry(".htm", newHtmlCompiler(html, Files::readString))
            , entry(".html", newHtmlCompiler(html, Files::readString))
            , entry(".hct", newHtmlCompiler(html, Files::readString))
            , entry(".css", newScriptCompiler(logger, CssCompiler::compressCssCode, newNopCompiler(), ".min.css"))
            , entry(".scss", newScriptCompiler(logger, CssCompiler::compressCssCode, newScssCompiler(), ".min.css"))
            , entry(".sass", newScriptCompiler(logger, CssCompiler::compressCssCode, newSassCompiler(), ".min.css"))
            , entry(".stylus", newScriptCompiler(logger, CssCompiler::compressCssCode, newStylusCompiler(), ".min.css"))
            , entry(".js", newScriptCompiler(logger, JsCompiler::compressJavascriptCode, newNopCompiler(), ".min.js"))
            , entry(".jspp", newScriptCompiler(logger, JsCompiler::compressJavascriptCode, newJsppCompiler(), ".min.js"))
            , entry(".js++", newScriptCompiler(logger, JsCompiler::compressJavascriptCode, newJsppCompiler(), ".min.js"))
            , entry(".dart", newScriptCompiler(logger, JsCompiler::compressJavascriptCode, newDartCompiler(), ".min.js"))
            , entry(".ts", newScriptCompiler(logger, JsCompiler::compressJavascriptCode, newTypescriptCompiler(), ".min.js"))
            );
    }

}
