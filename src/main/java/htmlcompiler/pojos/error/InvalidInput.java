package htmlcompiler.pojos.error;

public final class InvalidInput extends Exception {
    public InvalidInput(final String message) {
        super(message);
    }
    public InvalidInput(final Exception cause) {
        super(cause);
    }
    public InvalidInput(final String message, final Exception cause) {
        super(message, cause);
    }
}
