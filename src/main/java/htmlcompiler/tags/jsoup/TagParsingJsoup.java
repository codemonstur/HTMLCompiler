package htmlcompiler.tags.jsoup;

import htmlcompiler.tools.Logger;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static htmlcompiler.model.ImageType.toMimeType;
import static htmlcompiler.tools.Coding.encodeBase64;
import static htmlcompiler.tools.Coding.sha384;
import static htmlcompiler.tools.HTTP.*;
import static htmlcompiler.tools.IO.toByteArray;
import static java.lang.String.format;

public enum TagParsingJsoup {;

    public static void copyAttributes(final Element from, final Element to) {
        for (final Attribute attribute : from.attributes()) {
            to.attr(attribute.getKey(), attribute.getValue());
        }
    }

    public static Element removeAttributes(final Element element, final String... attributes) {
        for (final String attribute : attributes) {
            element.removeAttr(attribute);
        }
        return element;
    }

    public static boolean isLinkFavicon(final Element element) {
        return element.hasAttr("rel")
            && ( element.attr("rel").equalsIgnoreCase("icon")
               ||
                 element.attr("rel").equalsIgnoreCase("shortcut icon")
               );
    }
    public static boolean isLinkStyleSheet(final Element element) {
        return element.hasAttr("rel") && element.attr("rel").equalsIgnoreCase("stylesheet");
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
        final String path = element.attr(attribute);
        if (path != null && !isUrl(path)) {
            element.attr(attribute, "/"+path);
            element.removeAttr("to-absolute");
        }
    }

    public static void addIntegrityAttributes(final Element element, final String url
            , final File file, final Logger log) throws IOException, NoSuchAlgorithmException {
        try {
            if (isUrl(url) && (element.hasAttr("force-integrity") || urlHasCorsAllowed(url))) {
                element.attr("integrity", toIntegrityValue(urlToByteArray(url)));
                element.removeAttr("force-integrity");
                if (!element.hasAttr("crossorigin")) element.attr("crossorigin", "anonymous");
                log.warn(format("File %s has tag without integrity, rewrote to: %s", file.toPath().normalize(), element.html()));
            }
        } catch (IOException e) {
            log.warn("Failed to get data for tag src/href attribute " + url + ", error is " + e.getMessage());
            throw e;
        }
    }

    public static boolean isInlineScript(final Element node) {
        if (node == null) return false;
        if (!"script".equals(node.tagName())) return false;

        return !node.hasAttr("src")
            && ( !node.hasAttr("type")
              || "text/javascript".equalsIgnoreCase(node.attr("type"))
               );
    }
    public static boolean isInlineStyle(final Element node) {
        return node != null && "style".equals(node.tagName());
    }

    public static boolean isEmpty(final Element script) {
        final String code = script.text();
        return code == null || code.trim().isEmpty();
    }

    public static boolean isHtml(final Element script) {
        return script.hasAttr("type")
            && script.attr("type").equalsIgnoreCase("text/html");
    }

    public static boolean isCss(final Element link) {
        return !link.hasAttr("type")
            || link.attr("type").equalsIgnoreCase("text/css");
    }

    public static Element previousElementSibling(final Element element) {
        Element previous = element.previousElementSibling();
        while (previous != null) {
            if (previous.hasAttr("htmlcompiler")) {
                previous.remove();
                previous = element.previousElementSibling();
            } else {
                break;
            }
        }

        return previous;
    }

    public static void replaceWith(final Element original, final Element replacement) {
        original.attr("htmlcompiler", "delete-me");
        original.after(replacement);
    }

    public static void replaceWith(final Element original, final Elements replacements) {
        original.attr("htmlcompiler", "delete-me");
        for (int i = replacements.size()-1; i >= 0; i--) {
            original.after(replacements.get(i));
        }
    }

}
