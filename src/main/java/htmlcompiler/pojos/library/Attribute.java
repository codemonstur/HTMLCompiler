package htmlcompiler.pojos.library;

public final class Attribute {
    public final String name;
    public final String value;

    private Attribute(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
}
