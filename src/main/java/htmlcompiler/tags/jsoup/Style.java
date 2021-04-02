package htmlcompiler.tags.jsoup;

import htmlcompiler.pojos.compile.StyleType;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Path;

import static htmlcompiler.compilers.scripts.CssCompiler.compressCssCode;
import static htmlcompiler.pojos.compile.StyleType.css;
import static htmlcompiler.pojos.compile.StyleType.detectStyleType;
import static htmlcompiler.tags.jsoup.TagParsingJsoup.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Style {;

    public static TagVisitor newStyleVisitor() {
        return (TailVisitor) (config, file, element, depth) -> {
            if (element.hasAttr("inline")) {
                final Path location = toLocation(file, element.attr("src"), "style tag in %s has an invalid src location '%s'");

                final StyleType type = detectStyleType(element, css);
                setData(element, compressIfRequested(element, type.compile(location)));
                removeAttributes(element, "inline", "compress", "src", "type");

                final Element previousSibling = previousElementSibling(element);
                if (isInlineStyle(previousSibling) && !isScriptEmpty(previousSibling)) {
                    setData(element, previousSibling.data() + element.data());
                    previousSibling.attr("htmlcompiler", "delete-me");
                }
                return;
            }

            if (!isStyleEmpty(element)) {
                final StyleType type = detectStyleType(element, css);
                setData(element, compressIfRequested(element, type.compile(element.data(), file)));
                removeAttributes(element,"compress", "type");

                final Element previousSibling = previousElementSibling(element);
                if (isInlineStyle(previousSibling) && !isStyleEmpty(previousSibling)) {
                    setData(element, previousSibling.data() + element.data());
                    previousSibling.attr("htmlcompiler", "delete-me");
                }

                return;
            }
            if (element.hasAttr("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
        };
    }

    private static String compressIfRequested(final Element element, final String code) throws IOException {
        if (code == null || code.isEmpty()) return code;
        return element.hasAttr("compress") ? compressCssCode(code) : code;
    }

}
