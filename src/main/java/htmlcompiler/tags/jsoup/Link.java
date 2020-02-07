package htmlcompiler.tags.jsoup;

import htmlcompiler.pojos.compile.StyleType;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import htmlcompiler.tools.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.nio.file.Path;

import static htmlcompiler.compilers.scripts.CssCompiler.compressCssCode;
import static htmlcompiler.pojos.compile.ImageType.toMimeType;
import static htmlcompiler.pojos.compile.StyleType.css;
import static htmlcompiler.pojos.compile.StyleType.detectStyleType;
import static htmlcompiler.tags.jsoup.TagParsingJsoup.*;
import static htmlcompiler.tools.IO.toLocation;
import static java.nio.file.Files.readAllBytes;

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

    private static void inlineFavicon(final Node element, final Path file) throws InvalidInput, IOException {
        final Path location = toLocation(file, element.attr("href"), "<link> in %s has an invalid href location '%s'");
        final String type = (element.hasAttr("type")) ? element.attr("type") : toMimeType(location);
        element.removeAttr("inline");
        element.attr("href", toDataUrl(type, readAllBytes(file)));
    }

    private static Element inlineStylesheet(final Element element, final Path file, final Document document)
            throws Exception {
        final Path location = toLocation(file, element.attr("href"), "<link> in %s has an invalid href location '%s'");

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
