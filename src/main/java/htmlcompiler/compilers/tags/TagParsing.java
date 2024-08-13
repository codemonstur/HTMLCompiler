package htmlcompiler.compilers.tags;

import htmlcompiler.utils.Logger;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import static htmlcompiler.pojos.compile.ImageType.toMimeType;
import static htmlcompiler.services.RepositoryHashes.uriToIntegrityValue;
import static htmlcompiler.utils.Coding.encodeBase64;
import static htmlcompiler.utils.HTTP.isUrl;

public enum TagParsing {;

    public static void copyAttributes(final Element from, final Element to) {
        for (final Attribute attribute : from.attributes()) {
            to.removeAttr(attribute.getKey());
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

    public static String toDataUrl(final Path location) throws IOException {
        return toDataUrl(toMimeType(location), location);
    }
    public static String toDataUrl(final String type, final Path location) throws IOException {
        return toDataUrl(type, Files.readAllBytes(location));
    }
    public static String toDataUrl(final String type, final byte[] data) {
        return "data:"+type+";base64,"+encodeBase64(data);
    }


    public static void makeAbsolutePath(final Element element, final String attribute) {
        final String path = element.attr(attribute);
        if (path != null && !isUrl(path)) {
            element.attr(attribute, "/"+path);
            element.removeAttr("to-absolute");
        }
    }

    public static void addIntegrityAttributes(final Element element, final String url
            , final Logger log) throws IOException, NoSuchAlgorithmException {
        try {
            if (isUrl(url)) {
                element.attr("integrity", uriToIntegrityValue(url));
                if (!element.hasAttr("crossorigin"))
                    element.attr("crossorigin", "anonymous");
                if (!element.hasAttr("referrerpolicy"))
                    element.attr("referrerpolicy", "no-referrer");
            }
        } catch (final IOException e) {
            log.warn("Failed to get data for tag src/href attribute " + url);
            log.warn(e.getMessage());
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

    public static boolean isScriptEmpty(final Element script) {
        final String code = script.data();
        return code == null || code.trim().isEmpty();
    }
    public static boolean isStyleEmpty(final Element style) {
        final String code = style.data();
        return code == null || code.trim().isEmpty();
    }

    public static void setData(final Element script, final String data) {
        for (final DataNode dataNode : script.dataNodes()) {
            dataNode.remove();
        }
        script.appendChild(new DataNode(data));
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
