package htmlcompiler.error;

import static java.lang.String.*;

public final class UnrecognizedCommand extends Exception {

    public UnrecognizedCommand(final String name) {
        super(format("Unrecognized command: '%s'", name));
    }

}
