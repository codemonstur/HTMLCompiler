package htmlcompiler.tags.neko;

import htmlcompiler.compilers.html.NekoCompiler;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.model.StyleType;
import htmlcompiler.tools.IO;
import htmlcompiler.tools.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;

import static htmlcompiler.compilers.CssCompiler.compressCssCode;
import static htmlcompiler.model.ImageType.toMimeType;
import static htmlcompiler.model.StyleType.css;
import static htmlcompiler.model.StyleType.detectStyleType;
import static htmlcompiler.tags.neko.TagParsingNeko.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Link {;

    public static TagProcessor newLinkProcessor(final NekoCompiler html, final Logger log) {
        return (file, document, element) -> {
            if (isLinkFavicon(element) && element.hasAttribute("inline")) {
                inlineFavicon(element, file);
                return true;
            }
            if (isLinkStyleSheet(element) && element.hasAttribute("inline")) {
                inlineStylesheet(element, file, document);

/*
                This code is supposed to merge adjacent tags together. It does not work.

                final Node previousSibling = getPreviousTagSibling(style, null);
                if (isInlineStyle(previousSibling) && !isEmpty(previousSibling)) {
                    style.setTextContent(previousSibling.getTextContent() + style.getTextContent());
                    style.getParentNode().removeChild(previousSibling);
                }
*/

                return false;
            }
            if (!element.hasAttribute("integrity") && !element.hasAttribute("no-integrity")) {
                addIntegrityAttributes(element, element.getAttribute("href"), file, html, log);
            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "href");
            }
            removeAttributes(element, "to-absolute", "no-integrity");
            return false;
        };
    }

    private static void inlineFavicon(final Element element, final File file) throws InvalidInput, IOException {
        final File location = toLocation(file, element.getAttribute("href"), "<link> in %s has an invalid href location '%s'");
        final String type = (element.hasAttribute("type")) ? element.getAttribute("type") : toMimeType(location.getName());
        element.removeAttribute("inline");
        element.setAttribute("href", toDataUrl(type, IO.toByteArray(file)));
    }

    private static void inlineStylesheet(final Element element, final File file, final Document document)
            throws Exception {
        final File location = toLocation(file, element.getAttribute("href"), "<link> in %s has an invalid href location '%s'");

        final Element style = document.createElement("style");
        final StyleType type = detectStyleType(element, css);
        style.setTextContent(type.compile(location));

        if (element.hasAttribute("compress"))
            style.setTextContent(compressCssCode(style.getTextContent()));

        copyAttributes(element, style);
        removeAttributes(style, "href", "rel", "inline", "compress");

        replaceTag(element, style);
    }

}
