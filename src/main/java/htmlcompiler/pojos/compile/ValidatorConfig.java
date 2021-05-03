package htmlcompiler.pojos.compile;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ValidatorConfig {

    @SerializedName("text_node_parents_have_attributes")
    public final List<String> textNodeParentsHaveAttributes;
    @SerializedName("sibling_attributes")
    public final Map<String, String> siblingAttributes;

    public ValidatorConfig() {
        this(new ArrayList<>(), new HashMap<>());
    }
    public ValidatorConfig(final List<String> textNodeParentsHaveAttributes, final Map<String, String> siblingAttributes) {
        this.textNodeParentsHaveAttributes = textNodeParentsHaveAttributes;
        this.siblingAttributes = siblingAttributes;
    }

}
