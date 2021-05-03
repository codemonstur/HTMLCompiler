package htmlcompiler.compilers.tags;

import htmlcompiler.pojos.compile.StyleType;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.tools.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.nio.file.Path;

import static htmlcompiler.compilers.CssCompiler.compressCssCode;
import static htmlcompiler.pojos.compile.ImageType.toMimeType;
import static htmlcompiler.pojos.compile.StyleType.css;
import static htmlcompiler.pojos.compile.StyleType.detectStyleType;
import static htmlcompiler.services.RepositoryVersions.checkVersionLibrary;
import static htmlcompiler.tools.IO.toLocation;
import static java.nio.file.Files.readAllBytes;

public enum Link {;

    public static TagVisitor newLinkVisitor(final Logger log) {
        return (TagVisitor.TailVisitor) (config, file, node, depth) -> {
            if (TagParsing.isLinkFavicon(node) && node.hasAttr("inline")) {
                inlineFavicon(node, file);
                return;
            }
            if (TagParsing.isLinkStyleSheet(node) && node.hasAttr("href"))
                checkVersionLibrary(log, file.toString(), node.attr("href"), config.ignoreMajorVersions);

            if (TagParsing.isLinkStyleSheet(node) && node.hasAttr("inline")) {
                final Element style = inlineStylesheet(node, file, node.ownerDocument());

                final Element previousSibling = TagParsing.previousElementSibling(node);
                if (TagParsing.isInlineStyle(previousSibling) && !TagParsing.isStyleEmpty(previousSibling)) {
                    TagParsing.setData(style, previousSibling.data() + style.data());
                    previousSibling.attr("htmlcompiler", "delete-me");
                }

                TagParsing.replaceWith(node, style);
                return;
            }
            if (!node.hasAttr("integrity") && !node.hasAttr("no-integrity")) {
                TagParsing.addIntegrityAttributes(node, node.attr("href"), log);
            }
            if (node.hasAttr("to-absolute")) {
                TagParsing.makeAbsolutePath(node, "href");
            }
            TagParsing.removeAttributes(node, "to-absolute", "no-integrity");
        };
    }

    private static void inlineFavicon(final Node element, final Path file) throws InvalidInput, IOException {
        final Path location = toLocation(file, element.attr("href"), "<link> in %s has an invalid href location '%s'");
        final String type = (element.hasAttr("type")) ? element.attr("type") : toMimeType(location);
        element.removeAttr("inline");
        element.attr("href", TagParsing.toDataUrl(type, readAllBytes(file)));
    }

    private static Element inlineStylesheet(final Element element, final Path file, final Document document)
            throws Exception {
        final Path location = toLocation(file, element.attr("href"), "<link> in %s has an invalid href location '%s'");

        final Element style = document.createElement("style");
        final StyleType type = detectStyleType(element, css);
        TagParsing.setData(style, type.compile(location));

        if (element.hasAttr("compress"))
            TagParsing.setData(style, compressCssCode(style.data()));

        TagParsing.removeAttributes(element, "href", "rel", "inline", "compress");
        TagParsing.copyAttributes(element, style);
        return style;
    }

}
