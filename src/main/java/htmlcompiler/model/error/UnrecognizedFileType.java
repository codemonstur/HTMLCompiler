package htmlcompiler.model.error;

public final class UnrecognizedFileType extends Exception {

    public UnrecognizedFileType(final String name) {
        super(String.format("Unrecognized filetype: '%s'", name));
    }
    public UnrecognizedFileType(final String name, final Throwable cause) {
        super(String.format("Unrecognized filetype: '%s'", name), cause);
    }

}
