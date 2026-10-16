package htmlcompiler.pojos.compile;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ValidatorConfig(
        @SerializedName("text_node_parents_have_attributes") List<String> textNodeParentsHaveAttributes,
        @SerializedName("sibling_attributes") Map<String, String> siblingAttributes) {

    public ValidatorConfig() {
        this(new ArrayList<>(), new HashMap<>());
    }

    /** Same as CompilerConfig.withDefaults: omitted components come in as null. */
    static ValidatorConfig withDefaults(final ValidatorConfig config) {
        if (config == null) return new ValidatorConfig();
        return new ValidatorConfig(
                config.textNodeParentsHaveAttributes() != null ? config.textNodeParentsHaveAttributes() : new ArrayList<>(),
                config.siblingAttributes() != null ? config.siblingAttributes() : new HashMap<>());
    }

}
