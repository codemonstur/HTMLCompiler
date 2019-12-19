package htmlcompiler.tools;

import htmlcompiler.compile.HtmlCompiler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static htmlcompiler.model.ImageType.toMimeType;
import static htmlcompiler.tools.Coding.encodeBase64;
import static htmlcompiler.tools.Coding.sha384;
import static htmlcompiler.tools.HTTP.*;
import static htmlcompiler.tools.IO.toByteArray;
import static java.lang.String.format;

public enum HTML {;
    public static final String DOCTYPE = "<!DOCTYPE html>";

    public static void copyAttributes(final Element from, final Element to) {
        NamedNodeMap attributes = from.getAttributes(); Node attribute;
        for (int i = 0; i < attributes.getLength(); i++) {
            attribute = attributes.item(i);
            to.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
        }
    }

    public static Element removeAttributes(final Element element, final String... attributes) {
        for (final String attribute : attributes) {
            element.removeAttribute(attribute);
        }
        return element;
    }

    public static void replaceTag(final Element original, final Element replacement) {
        Node parent = original.getParentNode();
        Node after = original.getNextSibling();
        parent.removeChild(original);
        if (after != null)
            parent.insertBefore(replacement, after);
        else
            parent.appendChild(replacement);
    }

    public static void deleteTag(final Element element) {
        element.getParentNode().removeChild(element);
    }

    public static Element newElementOf(final Document document, final File location, final HtmlCompiler compiler) throws Exception {
        return toElementOf(document, loadHtml(compiler, location));
    }

    public static Element toElementOf(final Document document, final Element element) {
        return (Element)document.importNode(element, true);
    }

    public static Element loadHtml(final HtmlCompiler html, final File location) throws Exception {
        final String content = IO.toString(location);
        if (content.isEmpty()) return null;
        return html.processHtml(location, html.htmlToDocument(content)).getDocumentElement();
    }

    public static boolean isLinkFavicon(final Element element) {
        return element.hasAttribute("rel") && (
                element.getAttribute("rel").equalsIgnoreCase("icon") ||
                        element.getAttribute("rel").equalsIgnoreCase("shortcut icon"));
    }
    public static boolean isLinkStyleSheet(final Element element) {
        return element.hasAttribute("rel") && element.getAttribute("rel").equalsIgnoreCase("stylesheet");
    }

    public static String toDataUrl(final File location) throws IOException {
        return toDataUrl(toMimeType(location.getName()), location);
    }
    public static String toDataUrl(final String type, final File location) throws IOException {
        return toDataUrl(type, toByteArray(location));
    }
    public static String toDataUrl(final String type, final byte[] data) {
        return "data:"+type+";base64,"+encodeBase64(data);
    }

    public static String toIntegrityValue(final byte[] data) throws NoSuchAlgorithmException {
        return "sha384-"+encodeBase64(sha384(data));
    }

    public static void makeAbsolutePath(final Element element, final String attribute) {
        final String path = element.getAttribute(attribute);
        if (path != null && !isUrl(path)) {
            element.setAttribute(attribute, "/"+path);
            element.removeAttribute("to-absolute");
        }
    }

    public static void addIntegrityAttributes(final Element element, final String url, final File file
            , final HtmlCompiler html, final Logger log) throws IOException, NoSuchAlgorithmException, TransformerException {
        try {
            if (isUrl(url) && (element.hasAttribute("force-security") || urlHasCorsAllowed(url))) {
                element.setAttribute("integrity", toIntegrityValue(urlToByteArray(url)));
                if (!element.hasAttribute("crossorigin")) element.setAttribute("crossorigin", "anonymous");
                log.warn(format("File %s has tag without integrity, rewrote to: %s", file.toPath().normalize(), html.toHtml(element)));
            }
        } catch (IOException e) {
            log.warn("Failed to get data for tag src/href attribute " + url + ", error is " + e.getMessage());
            throw e;
        }
    }
}
