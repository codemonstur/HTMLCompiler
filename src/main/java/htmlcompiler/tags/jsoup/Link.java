package htmlcompiler.tags.jsoup;

import htmlcompiler.error.InvalidInput;
import htmlcompiler.model.StyleType;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import htmlcompiler.tools.IO;
import htmlcompiler.tools.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.File;
import java.io.IOException;

import static htmlcompiler.compilers.CssCompiler.compressCssCode;
import static htmlcompiler.model.ImageType.toMimeType;
import static htmlcompiler.model.StyleType.css;
import static htmlcompiler.model.StyleType.detectStyleType;
import static htmlcompiler.tags.jsoup.TagParsingJsoup.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Link {;

    public static TagVisitor newLinkVisitor(final Logger log) {
        return (TailVisitor) (file, node, depth) -> {
            if (isLinkFavicon(node) && node.hasAttr("inline")) {
                inlineFavicon(node, file);
                return;
            }
            if (isLinkStyleSheet(node) && node.hasAttr("inline")) {
                final Element style = inlineStylesheet(node, file, node.ownerDocument());

                final Element previousSibling = previousElementSibling(node);
                if (isInlineStyle(previousSibling) && !isStyleEmpty(previousSibling)) {
                    setData(style, previousSibling.data() + style.data());
                    previousSibling.attr("htmlcompiler", "delete-me");
                }

                replaceWith(node, style);
                return;
            }
            if (!node.hasAttr("integrity") && !node.hasAttr("no-integrity")) {
                addIntegrityAttributes(node, node.attr("href"), file, log);
            }
            if (node.hasAttr("to-absolute")) {
                makeAbsolutePath(node, "href");
            }
            removeAttributes(node, "to-absolute", "no-integrity");
        };
    }

    private static void inlineFavicon(final Node element, final File file) throws InvalidInput, IOException {
        final File location = toLocation(file, element.attr("href"), "<link> in %s has an invalid href location '%s'");
        final String type = (element.hasAttr("type")) ? element.attr("type") : toMimeType(location.getName());
        element.removeAttr("inline");
        element.attr("href", toDataUrl(type, IO.toByteArray(file)));
    }

    private static Element inlineStylesheet(final Element element, final File file, final Document document)
            throws Exception {
        final File location = toLocation(file, element.attr("href"), "<link> in %s has an invalid href location '%s'");

        final Element style = document.createElement("style");
        final StyleType type = detectStyleType(element, css);
        setData(style, type.compile(location));

        if (element.hasAttr("compress"))
            setData(style, compressCssCode(style.data()));

        removeAttributes(element, "href", "rel", "inline", "compress");
        copyAttributes(element, style);
        return style;
    }

}
