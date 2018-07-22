package htmlcompiler.model.extjs;

public final class TextNode implements Node {
    public final String text;

    public TextNode(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
