package htmlcompiler.pojos.compile;

import htmlcompiler.compilers.scripts.CodeCompiler;
import org.w3c.dom.Element;

import java.io.File;

import static htmlcompiler.compilers.scripts.CodeCompiler.newNopCompiler;
import static htmlcompiler.compilers.scripts.JsCompiler.*;

public enum ScriptType {
    minified_javascript(newNopCompiler()),
    javascript(newNopCompiler()),
    typescript(newTypescriptCompiler()),
    jspp(newJsppCompiler()),
    dart(newDartCompiler());

    private final CodeCompiler compiler;
    ScriptType(final CodeCompiler compiler) {
        this.compiler = compiler;
    }
    public String compile(final String jsCode, final File parent) throws Exception {
        return compiler.compileCode(jsCode, parent);
    }
    public String compile(final File location) throws Exception {
        return compiler.compileCode(location);
    }

    public static ScriptType detectScriptType(final Element element, final ScriptType defaultValue) {
        if (element.hasAttribute("type"))
            return contentTypeToScriptType(element.getAttribute("type"), defaultValue);
        if (element.hasAttribute("src"))
            return filenameToScriptType(element.getAttribute("src"), defaultValue);
        return javascript;
    }

    public static ScriptType detectScriptType(final org.jsoup.nodes.Element element, final ScriptType defaultValue) {
        if (element.hasAttr("type"))
            return contentTypeToScriptType(element.attr("type"), defaultValue);
        if (element.hasAttr("src"))
            return filenameToScriptType(element.attr("src"), defaultValue);
        return javascript;
    }

    private static ScriptType contentTypeToScriptType(final String contentType, final ScriptType defaultValue) {
        if (contentType.equalsIgnoreCase("text/javascript")) return javascript;
        if (contentType.equalsIgnoreCase("text/typescript")) return typescript;
        if (contentType.equalsIgnoreCase("text/jspp")) return jspp;
        if (contentType.equalsIgnoreCase("text/js++")) return jspp;
        if (contentType.equalsIgnoreCase("text/dart")) return dart;
        return defaultValue;
    }

    private static ScriptType filenameToScriptType(final String filename, final ScriptType defaultValue) {
        if (filename.endsWith(".min.js")) return minified_javascript;
        if (filename.endsWith(".js")) return javascript;
        if (filename.endsWith(".ts")) return typescript;
        if (filename.endsWith(".tsc")) return typescript;
        if (filename.endsWith(".jspp")) return jspp;
        if (filename.endsWith(".js++")) return jspp;
        if (filename.endsWith(".dart")) return dart;
        return defaultValue;
    }

}
