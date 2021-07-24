package htmlcompiler.compilers.checks;

import htmlcompiler.pojos.compile.CompilerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;

public final class CheckListBuilder {

    public static CheckListBuilder newJsoupCheckList() {
        return new CheckListBuilder(new CompilerConfig());
    }
    public static CheckListBuilder newJsoupCheckList(final CompilerConfig conf) {
        return new CheckListBuilder(conf);
    }

    private final List<ElementChecks.JsoupElementCheck> list;
    private final CompilerConfig config;

    private CheckListBuilder(final CompilerConfig config) {
        this.list = new ArrayList<>();
        this.config = config;
    }
    public CheckListBuilder addConfiguration(final Map<String, Boolean> conf) {
        this.config.checks.putAll(conf);
        return this;
    }

    public CheckListBuilder addIfEnabled(final String name, final ElementChecks.JsoupElementCheck check) {
        if (config.checks.getOrDefault(name, TRUE)) list.add(check);
        return this;
    }

    public List<ElementChecks.JsoupElementCheck> build() {
        return list;
    }

    public CheckListBuilder addAllEnabled() {
        addIfEnabled("dont_use_marquee", ElementChecks::dontUseMarquee);
        addIfEnabled("dont_use_blink", ElementChecks::dontUseBlink);
        addIfEnabled("has_border_attribute", ElementChecks::hasBorderAttribute);
        addIfEnabled("has_style_attribute", ElementChecks::hasStyleAttribute);
        addIfEnabled("has_uppercase_tags_or_attributes", ElementChecks::hasUppercaseTagsOrAttributes);
        addIfEnabled("missing_alt_for_images", ElementChecks::missingAltForImages);
        addIfEnabled("missing_placeholder_for_inputs", ElementChecks::missingPlaceholderForInputs);
        addIfEnabled("missing_pattern_for_inputs", ElementChecks::missingPatternForInputs);
        addIfEnabled("missing_input_type", ElementChecks::missingInputType);
        addIfEnabled("unknown_input_type", ElementChecks::unknownInputType);
        addIfEnabled("dont_use_bold", ElementChecks::dontUseBold);
        addIfEnabled("dont_use_italic", ElementChecks::dontUseItalic);
        addIfEnabled("dont_use_strong", ElementChecks::dontUseStrong);
        addIfEnabled("dont_use_em", ElementChecks::dontUseEm);
        addIfEnabled("dont_use_styling", ElementChecks::dontUseStyling);
        addIfEnabled("marginwidth_in_body", ElementChecks::marginWidthInBody);
        addIfEnabled("align_attribute_contains_absmiddle", ElementChecks::alignAttributeContainsAbsmiddle);
        addIfEnabled("has_deprecated_tag", ElementChecks::hasDeprecatedTag);
        addIfEnabled("has_deprecated_attribute", ElementChecks::hasDeprecatedAttribute);
        addIfEnabled("script_with_hardcoded_nonce", ElementChecks::scriptWithHardcodedNonce);
        addIfEnabled("style_with_hardcoded_nonce", ElementChecks::styleWithHardcodedNonce);
        addIfEnabled("label_with_for_attribute", ElementChecks::labelWithForAttribute);
        addIfEnabled("input_without_maxlength", ElementChecks::inputWithoutMaxLength);
        addIfEnabled("has_event_handler_attribute", ElementChecks::hasEventHandlerAttribute);
        addIfEnabled("is_valid_tag", ElementChecks::isValidTag);
        addIfEnabled("is_valid_attribute", ElementChecks::isValidAttribute);
        addIfEnabled("button_has_href", ElementChecks::buttonHasHref);
        return this;
    }
}
