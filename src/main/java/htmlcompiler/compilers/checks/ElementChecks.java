package htmlcompiler.compilers.checks;

import htmlcompiler.pojos.compile.CompilerConfig;
import htmlcompiler.tools.Logger;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.util.Set;

import static htmlcompiler.tools.Filenames.toRelativePath;
import static htmlcompiler.tools.HTML.*;
import static htmlcompiler.tools.Strings.hasUppercase;
import static xmlparser.utils.Functions.isNullOrEmpty;

public enum ElementChecks {;

    public interface JsoupElementCheck {
        void checkElement(Logger log, CompilerConfig config, Path file, Element element);
    }

    public static void buttonHasHref(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("button".equals(element.tagName()) && element.hasAttr("href"))
            log.warn("File " + toRelativePath(file) + " contains a <button> with an 'href=' attribute");
    }

    public static void hasStyleAttribute(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        for (final var attribute : element.attributes()) {
            if ("style".equalsIgnoreCase(attribute.getKey())) {
                log.warn("File " + toRelativePath(file) + " contains a 'style=' attribute");
            }
        }
    }

    public static void hasUppercaseTagsOrAttributes(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if (hasUppercase(element.tagName())) {
            log.warn("File " + toRelativePath(file) + " contains a tag <" + element.tagName() + "> with upper case letters");
        }

        for (final var attribute : element.attributes()) {
            if (hasUppercase(attribute.getKey())) {
                log.warn("File " + toRelativePath(file) + " contains an attribute " + attribute.getKey() + " with upper case letters");
            }
        }
    }

    public static void missingAltForImages(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("img".equals(element.tagName()) && isNullOrEmpty(element.attr("alt")))
            log.warn("File " + toRelativePath(file) + " contains an <img> tag without an alt attribute");
    }

    public static void missingPlaceholderForInputs(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("input".equals(element.tagName()) && "text".equals(element.attr("type"))
         && !element.hasAttr("disabled") && isNullOrEmpty(element.attr("placeholder")))
            log.warn("File " + toRelativePath(file) + " contains an <input> tag without a placeholder attribute");
    }

    public static void missingInputType(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if (!"input".equals(element.tagName())) return;
        final var type = element.attr("type");
        if (type == null || type.isEmpty()) {
            log.warn("File " + toRelativePath(file) + " contains an <input> tag without a type attribute");
        }
    }

    public static void unknownInputType(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if (!"input".equals(element.tagName())) return;
        final var type = element.attr("type");
        if (type == null || type.isEmpty()) return;
        if (config.ignoreInputTypes.contains(type)) return;
        if (known_input_types.contains(type)) return;

        log.warn("File " + toRelativePath(file) + " contains an <input> tag with unknown type attribute '" + type + "'");
    }

    public static void dontUseMarquee(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("marquee".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains a <marquee> tag");
    }

    public static void dontUseBlink(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("blink".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains a <blink> tag");
    }

    public static void dontUseBold(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("b".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains a <b> tag, rewrite with <strong>");
    }

    public static void dontUseItalic(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("i".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains an <i> tag, rewrite with <em>");
    }

    public static void dontUseStrong(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("strong".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains a <strong> tag, rewrite with <b>");
    }

    public static void dontUseEm(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("em".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains an <em> tag, rewrite with <i>");
    }

    public static void dontUseStyling(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("b".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains a <b> tag, use CSS");

        if ("i".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains an <i> tag, use CSS");

        if ("strong".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains a <strong> tag, use CSS");

        if ("em".equalsIgnoreCase(element.tagName()))
            log.warn("File " + toRelativePath(file) + " contains an <em> tag, use CSS");
    }

    public static void marginWidthInBody(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("body".equalsIgnoreCase(element.tagName()) && element.hasAttr("marginwidth"))
            log.warn("File " + toRelativePath(file) + " has a body tag that uses marginwidth attribute");
    }

    public static void alignAttributeContainsAbsmiddle(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("absmiddle".equalsIgnoreCase(element.attr("align")))
            log.warn("File " + toRelativePath(file) + " has an align attribute with absmiddle as value");
    }

    public static void hasBorderAttribute(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if (element.hasAttr("border"))
            log.warn("File " + toRelativePath(file) + " has a tag with a border attribute");
    }

    public static void hasDeprecatedTag(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if (deprecated_tags.contains(element.tagName()))
            log.warn("File " + toRelativePath(file) + " has a deprecated tag <" + element.tagName() + ">");
    }

    public static void hasDeprecatedAttribute(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        for (final var attribute : element.attributes()) {
            final Set<String> tags = deprecated_attributes.get(attribute.getKey());
            if (tags != null && tags.contains(element.tagName())) {
                log.warn("File " + toRelativePath(file) + " has a deprecated attribute " + attribute.getKey() + " for tag <" + element.tagName() + ">");
            }
        }
    }

    public static void scriptWithHardcodedNonce(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("script".equalsIgnoreCase(element.tagName()) && !isNullOrEmpty(element.attr("nonce")))
            log.warn("File " + toRelativePath(file) + " has a <script> tag with a hardcoded nonce attribute");
    }

    public static void styleWithHardcodedNonce(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("style".equalsIgnoreCase(element.tagName()) && !isNullOrEmpty(element.attr("nonce")))
            log.warn("File " + toRelativePath(file) + " has a <style> tag with a hardcoded nonce attribute");
    }

    // Using label tag with for attribute: https://medium.com/@joelennon/common-html-mistakes-de28db16b964
    public static void labelWithForAttribute(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ("label".equalsIgnoreCase(element.tagName()) && element.hasAttr("for"))
            log.warn("File " + toRelativePath(file) + " has a <label> tag with a for attribute, use <label>Text<input></label>");
    }

    // Warn on form elements without validation; https://html.spec.whatwg.org/multipage/forms.html#client-side-form-validation
    public static void inputWithoutMaxLength(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        if ( "input".equalsIgnoreCase(element.tagName())
          && "text".equalsIgnoreCase(element.attr("type"))
          && !element.hasAttr("disabled") && !element.hasAttr("maxlength")) {
            log.warn("File " + toRelativePath(file) + " has a text <input> without a maxlength attribute");
        }
    }

    public static void hasEventHandlerAttribute(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        for (final var attribute : element.attributes()) {
            if (event_handler_attributes.contains(attribute.getKey().toLowerCase()))
                log.warn("File " + toRelativePath(file) + " has a <" + element.tagName() + "> tag with an event handler attribute " + attribute.getKey());
        }
    }

    public static void isValidTag(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        final String tagName = element.tagName().toLowerCase();
        if (config.ignoreTags.contains(tagName)) return;
        if (!known_tags.contains(tagName))
            log.warn("File " + toRelativePath(file) + " contains an unknown tag <" + element.tagName() + ">");
    }

    public static void isValidAttribute(final Logger log, final CompilerConfig config, final Path file, final Element element) {
        for (final var attribute : element.attributes()) {
            final String lowerAttr = attribute.getKey().toLowerCase();
            if (config.ignoreAttributes.contains(lowerAttr)) continue;
            if (!known_attributes.contains(lowerAttr) && !lowerAttr.startsWith("data-"))
                log.warn("File " + toRelativePath(file) + " contains a tag <" + element.tagName() + "> with an unknown attribute " + attribute.getKey());
        }
    }

}
