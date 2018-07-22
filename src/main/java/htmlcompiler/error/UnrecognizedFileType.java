package htmlcompiler.error;

public final class UnrecognizedFileType extends Exception {

    public final String name;
    public UnrecognizedFileType(final String name) {
        super(String.format("Unrecognized filetype: '%s'", name));
        this.name = name;
    }
    public UnrecognizedFileType(final String name, final Throwable cause) {
        super(String.format("Unrecognized filetype: '%s'", name), cause);
        this.name = name;
    }

}
