package htmlcompiler.tags.neko;

import htmlcompiler.compilers.html.NekoCompiler;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.error.UnrecognizedFileType;
import htmlcompiler.tools.IO;
import htmlcompiler.tools.Logger;
import org.lesscss.LessException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;

import static htmlcompiler.compilers.css.CssCompiler.compileCssCode;
import static htmlcompiler.compilers.css.CssCompiler.compressCssCode;
import static htmlcompiler.model.ImageType.toMimeType;
import static htmlcompiler.model.StyleType.toStyleType;
import static htmlcompiler.tags.neko.TagProcessor.isEmpty;
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
                final Element style = inlineStylesheet(element, file, document);

                final Node previousSibling = getPreviousTagSibling(style, null);
                if (isInlineStyle(previousSibling) && !isEmpty(previousSibling)) {
                    style.setTextContent(previousSibling.getTextContent() + style.getTextContent());
                    style.getParentNode().removeChild(previousSibling);
                }

                return true;
            }
            if (!element.hasAttribute("integrity") && !element.hasAttribute("no-security")) {
                addIntegrityAttributes(element, element.getAttribute("href"), file, html, log);
            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "href");
            }
            removeAttributes(element, "to-absolute", "no-security");
            return false;
        };
    }

    private static void inlineFavicon(final Element element, final File file) throws InvalidInput, IOException {
        final File location = toLocation(file, element.getAttribute("href"), "<link> in %s has an invalid href location '%s'");
        final String type = (element.hasAttribute("type")) ? element.getAttribute("type") : toMimeType(location.getName());
        element.removeAttribute("inline");
        element.setAttribute("href", toDataUrl(type, IO.toByteArray(file)));
    }

    private static Element inlineStylesheet(final Element element, final File file, final Document document)
            throws InvalidInput, UnrecognizedFileType, IOException, LessException {
        final File location = toLocation(file, element.getAttribute("href"), "<link> in %s has an invalid href location '%s'");

        final Element style = document.createElement("style");
        style.setTextContent(compileCssCode(toStyleType(location.getName()), IO.toString(location)));

        if (element.hasAttribute("compress"))
            style.setTextContent(compressCssCode(style.getTextContent()));

        copyAttributes(element, style);
        removeAttributes(style, "href", "rel", "inline", "compress");

        replaceTag(element, style);

        return style;
    }

}
