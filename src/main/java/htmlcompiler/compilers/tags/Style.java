package htmlcompiler.compilers.tags;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.compilers.tags.TagVisitor.TailVisitor;
import htmlcompiler.pojos.compile.StyleType;
import htmlcompiler.utils.Logger;
import org.jsoup.nodes.Element;

import java.nio.file.Path;

import static htmlcompiler.compilers.tags.TagParsing.*;
import static htmlcompiler.pojos.compile.StyleType.css;
import static htmlcompiler.pojos.compile.StyleType.detectStyleType;
import static htmlcompiler.utils.IO.toLocation;
import static htmlcompiler.utils.Strings.isNullOrEmpty;

public enum Style {;

    public static TagVisitor newStyleVisitor(final Logger log, final HtmlCompiler html) {
        return (TailVisitor) (config, file, element, depth) -> {
            if (element.hasAttr("inline")) {
                final Path location = toLocation(file, element.attr("src"), "style tag in %s has an invalid src location '%s'");

                final StyleType type = detectStyleType(element, css);
                final String code = type.compile(location);
                setData(element, shouldCompress(code, element) ? html.compressCss(code) : code);
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
                final String code = type.compile(element.data(), file);
                setData(element, shouldCompress(code, element) ? html.compressCss(code) : code);
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

    private static boolean shouldCompress(final String code, final Element element) {
        return !isNullOrEmpty(code) && element.hasAttr("compress");
    }

}
