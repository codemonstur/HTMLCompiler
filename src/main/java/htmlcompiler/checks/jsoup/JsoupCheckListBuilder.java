package htmlcompiler.checks.jsoup;

import htmlcompiler.checks.jsoup.JsoupElementChecks.JsoupElementCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsoupCheckListBuilder {

    public static JsoupCheckListBuilder newJsoupCheckList() {
        return new JsoupCheckListBuilder(new HashMap<>());
    }
    public static JsoupCheckListBuilder newJsoupCheckList(final Map<String, Boolean> conf) {
        return new JsoupCheckListBuilder(conf);
    }

    private final List<JsoupElementCheck> list;
    private final Map<String, Boolean> conf;

    private JsoupCheckListBuilder(final Map<String, Boolean> conf) {
        this.list = new ArrayList<>();
        this.conf = conf;
    }
    public JsoupCheckListBuilder addConfiguration(final Map<String, Boolean> conf) {
        this.conf.putAll(conf);
        return this;
    }

    public JsoupCheckListBuilder addIfEnabled(final String name, final JsoupElementCheck check) {
        if (conf.getOrDefault(name, Boolean.TRUE)) list.add(check);
        return this;
    }

    public List<JsoupElementCheck> build() {
        return list;
    }

    public JsoupCheckListBuilder addAllEnabled() {
        addIfEnabled("dont_use_marquee", JsoupElementChecks::dontUseMarquee);
        addIfEnabled("dont_use_blink", JsoupElementChecks::dontUseBlink);
        addIfEnabled("has_border_attribute", JsoupElementChecks::hasBorderAttribute);
        addIfEnabled("has_style_attribute", JsoupElementChecks::hasStyleAttribute);
        addIfEnabled("has_uppercase_tags_or_attributes", JsoupElementChecks::hasUppercaseTagsOrAttributes);
        addIfEnabled("missing_alt_for_images", JsoupElementChecks::missingAltForImages);
        addIfEnabled("missing_placeholder_for_inputs", JsoupElementChecks::missingPlaceholderForInputs);
        addIfEnabled("dont_use_bold", JsoupElementChecks::dontUseBold);
        addIfEnabled("dont_use_italic", JsoupElementChecks::dontUseItalic);
        addIfEnabled("dont_use_strong", JsoupElementChecks::dontUseStrong);
        addIfEnabled("dont_use_em", JsoupElementChecks::dontUseEm);
        addIfEnabled("dont_use_styling", JsoupElementChecks::dontUseStyling);
        addIfEnabled("marginwidth_in_body", JsoupElementChecks::marginWidthInBody);
        addIfEnabled("align_attribute_contains_absmiddle", JsoupElementChecks::alignAttributeContainsAbsmiddle);
        addIfEnabled("has_deprecated_tag", JsoupElementChecks::hasDeprecatedTag);
        addIfEnabled("has_deprecated_attribute", JsoupElementChecks::hasDeprecatedAttribute);
        addIfEnabled("script_with_hardcoded_nonce", JsoupElementChecks::scriptWithHardcodedNonce);
        addIfEnabled("style_with_hardcoded_nonce", JsoupElementChecks::styleWithHardcodedNonce);
        addIfEnabled("label_with_for_attribute", JsoupElementChecks::labelWithForAttribute);
        addIfEnabled("input_without_maxlength", JsoupElementChecks::inputWithoutMaxLength);
        addIfEnabled("has_event_handler_attribute", JsoupElementChecks::hasEventHandlerAttribute);
        addIfEnabled("is_valid_tag", JsoupElementChecks::isValidTag);
        addIfEnabled("is_valid_attribute", JsoupElementChecks::isValidAttribute);
        return this;
    }
}
