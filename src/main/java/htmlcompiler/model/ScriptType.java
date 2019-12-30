package htmlcompiler.model;

import htmlcompiler.compilers.js.ScriptCompiler;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;

import static htmlcompiler.compilers.js.ExtendedJSCompiler.newExtJSCompiler;
import static htmlcompiler.compilers.js.JsppCompiler.newJsppCompiler;
import static htmlcompiler.compilers.js.ScriptCompiler.newNopCompiler;
import static htmlcompiler.compilers.js.TypeScriptCompiler.newTypescriptCompiler;

public enum ScriptType {
    minified_javascript(newNopCompiler()),
    extended_javascript(newExtJSCompiler()),
    javascript(newNopCompiler()),
    typescript(newTypescriptCompiler()),
    jspp(newJsppCompiler());

    private final ScriptCompiler compiler;
    ScriptType(final ScriptCompiler compiler) {
        this.compiler = compiler;
    }
    public String compile(final String jsCode, final File parent) throws IOException {
        return compiler.compileScript(jsCode, parent);
    }
    public String compile(final File location) throws IOException {
        return compiler.compileScript(location);
    }

    public static ScriptType detectScriptType(final Element element, final ScriptType defaultValue) {
        final String contentType = element.getAttribute("type");
        if (contentType != null) return contentTypeToScriptType(contentType, defaultValue);
        final String fileName = element.getAttribute("src");
        if (fileName != null) return filenameToScriptType(fileName, defaultValue);
        return javascript;
    }

    public static ScriptType detectScriptType(final org.jsoup.nodes.Element element, final ScriptType defaultValue) {
        if (element.hasAttr("type")) {
            return contentTypeToScriptType(element.attr("type"), defaultValue);
        }
        if (element.hasAttr("src")) {
            return filenameToScriptType(element.attr("src"), defaultValue);
        }
        return javascript;
    }

    private static ScriptType contentTypeToScriptType(final String contentType, final ScriptType defaultValue) {
        if (contentType.equalsIgnoreCase("text/javascript")) return javascript;
        if (contentType.equalsIgnoreCase("text/extjs")) return extended_javascript;
        if (contentType.equalsIgnoreCase("text/typescript")) return typescript;
        if (contentType.equalsIgnoreCase("text/jspp")) return jspp;
        if (contentType.equalsIgnoreCase("text/js++")) return jspp;
        return defaultValue;
    }

    private static ScriptType filenameToScriptType(final String filename, final ScriptType defaultValue) {
        if (filename.endsWith(".min.js")) return minified_javascript;
        if (filename.endsWith(".ejs")) return extended_javascript;
        if (filename.endsWith(".js")) return javascript;
        if (filename.endsWith(".ts")) return typescript;
        if (filename.endsWith(".jspp")) return jspp;
        return defaultValue;
    }

}
