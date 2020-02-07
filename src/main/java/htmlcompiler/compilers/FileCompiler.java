package htmlcompiler.compilers;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.compilers.scripts.CodeCompiler;
import htmlcompiler.compilers.scripts.Compressor;
import htmlcompiler.compilers.scripts.CssCompiler;
import htmlcompiler.compilers.scripts.JsCompiler;
import htmlcompiler.compilers.templates.*;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.error.InvalidTemplate;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static htmlcompiler.compilers.scripts.CodeCompiler.newNopCompiler;
import static htmlcompiler.compilers.scripts.CssCompiler.*;
import static htmlcompiler.compilers.scripts.JsCompiler.*;
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
    private static FileCompiler newScriptCompiler(final Compressor compressor, final CodeCompiler compiler, final String extension) {
        return new FileCompiler() {
            public String compile(Path file) throws InvalidTemplate {
                try {
                    return compressor.compress(compiler.compileCode(file));
                } catch (Exception e) {
                    throw new InvalidTemplate(e);
                }
            }
            public String outputExtension() {
                return extension;
            }
        };
    }

    public static Map<String, FileCompiler> newFileCompilerMap(final HtmlCompiler html, final MavenProject project) {
        return Map.ofEntries
            ( entry(".pebble", newHtmlCompiler(html, new Pebble(project)))
            , entry(".jade", newHtmlCompiler(html, new Jade4j(project)))
            , entry(".pug", newHtmlCompiler(html, new Jade4j(project)))
            , entry(".md", newHtmlCompiler(html, new Markdown()))
            , entry(".hb", newHtmlCompiler(html, new Handlebars(project)))
            , entry(".jinjava", newHtmlCompiler(html, new JinJava(project)))
            , entry(".twig", newHtmlCompiler(html, new JTwig(project)))
            , entry(".mustache", newHtmlCompiler(html, new Mustache(project)))
            , entry(".thymeleaf", newHtmlCompiler(html, new Thymeleaf(project)))
            , entry(".htm", newHtmlCompiler(html, new NopEngine()))
            , entry(".html", newHtmlCompiler(html, new NopEngine()))
            , entry(".hct", newHtmlCompiler(html, new NopEngine()))
            , entry(".css", newScriptCompiler(CssCompiler::compressCssCode, newNopCompiler(), ".min.css"))
            , entry(".scss", newScriptCompiler(CssCompiler::compressCssCode, newScssCompiler(), ".min.css"))
            , entry(".sass", newScriptCompiler(CssCompiler::compressCssCode, newSassCompiler(), ".min.css"))
            , entry(".stylus", newScriptCompiler(CssCompiler::compressCssCode, newStylusCompiler(), ".min.css"))
            , entry(".js", newScriptCompiler(JsCompiler::compressJavascriptCode, newNopCompiler(), ".min.js"))
            , entry(".jspp", newScriptCompiler(JsCompiler::compressJavascriptCode, newJsppCompiler(), ".min.js"))
            , entry(".js++", newScriptCompiler(JsCompiler::compressJavascriptCode, newJsppCompiler(), ".min.js"))
            , entry(".dart", newScriptCompiler(JsCompiler::compressJavascriptCode, newDartCompiler(), ".min.js"))
            , entry(".ts", newScriptCompiler(JsCompiler::compressJavascriptCode, newTypescriptCompiler(), ".min.js"))
            );
    }

}
