package htmlcompiler.tools;

import java.util.function.Consumer;

public interface Logger {
    void info(String message);
    void warn(String message);

    static Logger newLogger(final Consumer<String> info, final Consumer<String> warn) {
        return new Logger() {
            public void info(String message) {
                info.accept(message);
            }
            public void warn(String message) {
                warn.accept(message);
            }
        };
    }

}
