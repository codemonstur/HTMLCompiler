package htmlcompiler.checks.jsoup;

import htmlcompiler.pojos.compile.ChecksConfig;
import htmlcompiler.tools.Logger;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static htmlcompiler.tools.HTML.known_attributes;
import static htmlcompiler.tools.HTML.known_tags;
import static htmlcompiler.tools.Strings.hasUppercase;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static xmlparser.utils.Functions.isNullOrEmpty;

public enum JsoupElementChecks {;

    public interface JsoupElementCheck {
        void checkElement(Logger log, ChecksConfig config, Path file, Element element);
    }

    public static void hasStyleAttribute(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        for (final var attribute : element.attributes()) {
            if ("style".equalsIgnoreCase(attribute.getKey())) {
                log.warn("File " + file + " contains a style attribute");
            }
        }
    }

    public static void hasUppercaseTagsOrAttributes(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if (hasUppercase(element.tagName())) {
            log.warn("File " + file + " contains a tag " + element.tagName() + " with upper case letters");
        }

        for (final var attribute : element.attributes()) {
            if (hasUppercase(attribute.getKey())) {
                log.warn("File " + file + " contains an attribute " + attribute.getKey() + " with upper case letters");
            }
        }
    }

    public static void missingAltForImages(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("img".equals(element.tagName()) && isNullOrEmpty(element.attr("alt")))
            log.warn("File " + file + " contains an img tag without an alt attribute");
    }

    public static void missingPlaceholderForInputs(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("input".equals(element.tagName()) && isNullOrEmpty(element.attr("placeholder")))
            log.warn("File " + file + " contains an input tag without a placeholder attribute");
    }

    public static void dontUseMarquee(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("marquee".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains a marquee tag");
    }

    public static void dontUseBlink(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("blink".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains a blink tag");
    }

    public static void dontUseBold(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("b".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains a b tag, rewrite with strong");
    }

    public static void dontUseItalic(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("i".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains an i tag, rewrite with em");
    }

    public static void dontUseStrong(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("strong".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains a strong tag, rewrite with b");
    }

    public static void dontUseEm(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("em".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains an em tag, rewrite with i");
    }

    public static void dontUseStyling(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("b".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains a b tag, use CSS");

        if ("i".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains an i tag, use CSS");

        if ("strong".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains a strong tag, use CSS");

        if ("em".equalsIgnoreCase(element.tagName()))
            log.warn("File " + file + " contains an em tag, use CSS");
    }

    public static void marginWidthInBody(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("body".equalsIgnoreCase(element.tagName()) && element.hasAttr("marginwidth"))
            log.warn("File " + file + " has a body tag that uses marginwidth attribute");
    }

    public static void alignAttributeContainsAbsmiddle(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("absmiddle".equalsIgnoreCase(element.attr("align")))
            log.warn("File " + file + " has an align attribute with absmiddle as value");
    }

    public static void hasBorderAttribute(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if (element.hasAttr("border"))
            log.warn("File " + file + " has a tag with a border attribute");
    }

    // https://uzzal.wordpress.com/2009/10/08/fobidden-deprecated-html-tags-and-attributes/
    // https://www.tutorialspoint.com/html5/html5_deprecated_tags.htm
    private static final Set<String> deprecated_tags = Set.of("acronym", "applet", "basefont", "big", "center"
            , "dir", "embed", "font", "frame", "frameset", "isindex", "noframes", "menu", "noembed", "s", "strike", "tt", "u");

    public static void hasDeprecatedTag(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if (deprecated_tags.contains(element.tagName()))
            log.warn("File " + file + " has a deprecated tag " + element.tagName());
    }

    // http://www.w3.org/TR/html4/index/elements.html
    // http://www.w3.org/TR/html4/index/attributes.html
    // https://uzzal.wordpress.com/2009/10/08/fobidden-deprecated-html-tags-and-attributes/
    // https://www.tutorialspoint.com/html5/html5_deprecated_tags.htm
    private static final Map<String, Set<String>> deprecated_attributes = ofEntries
        ( entry("abbr", Set.of("td", "t"))
        , entry("align", Set.of("caption", "iframe", "img", "input", "legend", "object", "table", "hr", "div", "h1"
             , "h2", "h3", "h4", "h5", "h6", "p", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"))
        , entry("alink", Set.of("body"))
        , entry("axis", Set.of("td", "t"))
        , entry("archive", Set.of("object"))
        , entry("background", Set.of("body"))
        , entry("bgcolor", Set.of("body", "table", "th", "tr", "td"))
        , entry("border", Set.of("img", "object", "table"))
        , entry("cellpadding", Set.of("table"))
        , entry("cellspacing", Set.of("table"))
        , entry("char", Set.of("col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"))
        , entry("charoff", Set.of("col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"))
        , entry("charset", Set.of("link", "a"))
        , entry("classid", Set.of("object"))
        , entry("clear", Set.of("br"))
        , entry("codebase", Set.of("object"))
        , entry("codetype", Set.of("object"))
        , entry("compact", Set.of("ol", "ul", "dl", "menu"))
        , entry("coords", Set.of("a"))
        , entry("declare", Set.of("object"))
        , entry("frame", Set.of("table"))
        , entry("frameborder", Set.of("iframe"))
        , entry("hspace", Set.of("img", "object"))
        , entry("link", Set.of("body"))
        , entry("longdesc", Set.of("img", "iframe"))
        , entry("marginheight", Set.of("iframe"))
        , entry("marginwidth", Set.of("iframe"))
        , entry("name", Set.of("img"))
        , entry("nohref", Set.of("area"))
        , entry("noshade", Set.of("hr"))
        , entry("nowrap", Set.of("td", "tr", "th"))
        , entry("profile", Set.of("head"))
        , entry("rev", Set.of("link", "a"))
        , entry("rules", Set.of("table"))
        , entry("scheme", Set.of("meta"))
        , entry("scope", Set.of("td"))
        , entry("scrolling", Set.of("iframe"))
        , entry("shape", Set.of("a"))
        , entry("size", Set.of("basefont", "font", "hr"))
        , entry("standby", Set.of("object"))
        , entry("start", Set.of("ol"))
        , entry("target", Set.of("link"))
        , entry("text", Set.of("body"))
        , entry("type", Set.of("li", "param", "ol", "ul"))
        , entry("valign", Set.of("col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"))
        , entry("value", Set.of("li"))
        , entry("valuetype", Set.of("param"))
        , entry("version", Set.of("html"))
        , entry("vlink", Set.of("body"))
        , entry("vspace", Set.of("img", "object"))
        , entry("width", Set.of("hr", "pre", "td", "th", "table", "col", "colgroup"))
        );

    public static void hasDeprecatedAttribute(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        for (final var attribute : element.attributes()) {
            final Set<String> tags = deprecated_attributes.get(attribute.getKey());
            if (tags != null && tags.contains(element.tagName())) {
                log.warn("File " + file + " has a deprecated attribute " + attribute.getKey() + " for tag " + element.tagName());
            }
        }
    }

    public static void scriptWithHardcodedNonce(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("script".equalsIgnoreCase(element.tagName()) && !isNullOrEmpty(element.attr("nonce")))
            log.warn("File " + file + " has a script with a hardcoded nonce attribute");
    }

    public static void styleWithHardcodedNonce(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("style".equalsIgnoreCase(element.tagName()) && !isNullOrEmpty(element.attr("nonce")))
            log.warn("File " + file + " has a script with a hardcoded nonce attribute");
    }


    // Using label tag with for attribute: https://medium.com/@joelennon/common-html-mistakes-de28db16b964
    public static void labelWithForAttribute(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ("label".equalsIgnoreCase(element.tagName()) && element.hasAttr("for"))
            log.warn("File " + file + " has a label tag with a for attribute, use <label>Text<input></label>");
    }

    // Warn on form elements without validation; https://html.spec.whatwg.org/multipage/forms.html#client-side-form-validation
    public static void inputWithoutMaxLength(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        if ( "input".equalsIgnoreCase(element.tagName())
          && "text".equalsIgnoreCase(element.attr("type"))
          && !element.hasAttr("maxlength")) {
            log.warn("File " + file + " has a text input without a maxlength attribute");
        }
    }

    private static final Set<String> event_handler_attributes = Set.of("onafterprint", "onbeforeprint"
            , "onbeforeunload", "onerror", "onhashchange", "onload", "onmessage", "onoffline", "ononline"
            , "onpagehide", "onpageshow", "onpopstate", "onresize", "onstorage", "onunload");

    public static void hasEventHandlerAttribute(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        for (final var attribute : element.attributes()) {
            if (event_handler_attributes.contains(attribute.getKey().toLowerCase()))
                log.warn("File " + file + " has a " + element.tagName() + " tag with an event handler attribute " + attribute.getKey());
        }
    }

    public static void isValidTag(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        final String tagName = element.tagName().toLowerCase();
        if (config.ignoreTags.contains(tagName)) return;
        if (!known_tags.contains(tagName))
            log.warn("File " + file + " contains an unknown tag " + element.tagName());
    }

    public static void isValidAttribute(final Logger log, final ChecksConfig config, final Path file, final Element element) {
        for (final var attribute : element.attributes()) {
            final String lowerAttr = attribute.getKey().toLowerCase();
            if (config.ignoreAttributes.contains(lowerAttr)) continue;
            if (!known_attributes.contains(lowerAttr) && !lowerAttr.startsWith("data-"))
                log.warn("File " + file + " contains a tag '" + element.tagName() + "' with an unknown attribute " + attribute.getKey());
        }
    }

}
