package htmlcompiler.pojos.compile;

import htmlcompiler.compilers.CodeCompiler;
import org.w3c.dom.Element;

import java.nio.file.Path;

import static htmlcompiler.compilers.CodeCompiler.newNopCompiler;
import static htmlcompiler.compilers.CssCompiler.*;

public enum StyleType {
    minified_css(newNopCompiler()),
    css(newNopCompiler()),
    less(newLessCompiler()),
    sass(newSassCompiler()),
    scss(newScssCompiler()),
    stylus(newStylusCompiler());

    private final CodeCompiler compiler;
    StyleType(final CodeCompiler compiler) {
        this.compiler = compiler;
    }
    public String compile(final String jsCode, final Path parent) throws Exception {
        return compiler.compileCode(jsCode, parent);
    }
    public String compile(final Path location) throws Exception {
        return compiler.compileCode(location);
    }

    public static StyleType detectStyleType(final Element element, final StyleType defaultValue) {
        if (element.hasAttribute("type"))
            return contentTypeToStyleType(element.getAttribute("type"), defaultValue);
        if (element.hasAttribute("href"))
            return filenameToStyleType(element.getAttribute("href"), defaultValue);
        return css;
    }

    public static StyleType detectStyleType(final org.jsoup.nodes.Element element, final StyleType defaultValue) {
        if (element.hasAttr("type"))
            return contentTypeToStyleType(element.attr("type"), defaultValue);
        if (element.hasAttr("href"))
            return filenameToStyleType(element.attr("href"), defaultValue);
        return css;
    }

    private static StyleType contentTypeToStyleType(final String contentType, final StyleType defaultValue) {
        if (contentType.equalsIgnoreCase("text/css")) return css;
        if (contentType.equalsIgnoreCase("text/less")) return less;
        if (contentType.equalsIgnoreCase("text/sass")) return sass;
        if (contentType.equalsIgnoreCase("text/scss")) return scss;
        if (contentType.equalsIgnoreCase("text/stylus")) return stylus;
        return defaultValue;
    }

    private static StyleType filenameToStyleType(final String filename, final StyleType defaultValue) {
        if (filename.endsWith(".min.css")) return minified_css;
        if (filename.endsWith(".css")) return css;
        if (filename.endsWith(".less")) return less;
        if (filename.endsWith(".scss")) return scss;
        if (filename.endsWith(".sass")) return sass;
        if (filename.endsWith(".stylus")) return stylus;
        if (filename.endsWith(".styl")) return stylus;
        return defaultValue;
    }

}

