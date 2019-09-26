package htmlcompiler.tools;

import org.apache.maven.plugin.logging.Log;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public interface Logger {
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    void info(String message);
    void warn(String message);

    static Logger newLogger(final Log log) {
        return newLogger(log::info, log::warn);
    }
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
