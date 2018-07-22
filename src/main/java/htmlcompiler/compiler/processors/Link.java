package htmlcompiler.compiler.processors;

import htmlcompiler.compiler.CssCompiler;
import htmlcompiler.compiler.HtmlCompiler;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.error.UnrecognizedFileType;
import htmlcompiler.logging.Logger;
import htmlcompiler.util.IO;
import htmlcompiler.util.Loader;
import org.lesscss.LessException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static htmlcompiler.compiler.CssCompiler.toStyleType;
import static htmlcompiler.util.Extensions.toMimeType;
import static htmlcompiler.util.HTML.*;
import static htmlcompiler.util.HTTP.isUrl;
import static htmlcompiler.util.HTTP.urlHasCorsAllowed;
import static java.lang.String.format;

public enum Link {;

    public static TagProcessor newLinkProcessor(final HtmlCompiler html, final Logger log, final CssCompiler css) {
        return (loader, file, document, element) -> {
            if (isLinkFavicon(element)) {
                return processFavicon(element, loader, file);
            }
            if (isLinkStyleSheet(element)) {
                if (processStylesheet(element, loader, file, document, css)) {
                    return true;
                }
            }
            if (!element.hasAttribute("integrity") && !element.hasAttribute("no-security")) {
                addIntegrityAttributes(element, loader, file, html, log);
            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "href");
            }
            removeAttributes(element, "to-absolute", "no-security");
            return false;
        };
    }

    private static boolean processFavicon(final Element element, final Loader loader, final File file) throws InvalidInput, IOException {
        if (element.hasAttribute("inline")) {
            final File location = loader.toLocation(file, element.getAttribute("href"), "<link> in %s has an invalid href location '%s'");
            final String type = (element.hasAttribute("type")) ? element.getAttribute("type") : toMimeType(location.getName());
            element.removeAttribute("inline");
            element.setAttribute("href", toDataUrl(type, IO.toByteArray(file)));
        } else if (element.hasAttribute("to-absolute")) {
            makeAbsolutePath(element, "href");
        }
        return false;
    }

    private static boolean processStylesheet(final Element element, final Loader loader, final File file
            , final Document document, final CssCompiler css) throws InvalidInput, UnrecognizedFileType, IOException, LessException {
        if (element.hasAttribute("inline")) {
            final File location = loader.toLocation(file, element.getAttribute("href"), "<link> in %s has an invalid href location '%s'");

            // create new style
            final Element style = document.createElement("style");
            style.setTextContent(css.compile(toStyleType(location.getName()), IO.toString(location)));

            if (element.hasAttribute("compress"))
                style.setTextContent(css.compress(style.getTextContent()));

            copyAttributes(element, style);
            removeAttributes(style, "href", "rel", "inline", "compress");

            replaceTag(element, style);
            return true;
        } else if (element.hasAttribute("to-absolute")) {
            makeAbsolutePath(element, "href");
        }
        return false;
    }

    private static void addIntegrityAttributes(final Element element, final Loader loader, final File file
            , final HtmlCompiler html, final Logger log) throws IOException, NoSuchAlgorithmException, TransformerException {
        final String url = element.getAttribute("href");
        if (isUrl(url) && urlHasCorsAllowed(url)) {
            element.setAttribute("integrity", toIntegrityValue(loader.getAsBytes(url)));
            if (!element.hasAttribute("crossorigin")) element.setAttribute("crossorigin", "anonymous");
            log.warn(format("File %s has <link> without integrity, rewrote to: %s", loader.relative(file.getAbsolutePath()), html.toHtml(element)));
        }
    }

}
