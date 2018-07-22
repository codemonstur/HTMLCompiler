package htmlcompiler.tools;

import org.apache.maven.plugin.logging.Log;

public interface Logger {
    void info(String message);
    void warn(String message);

    static Logger toLogger(final Log log) {
        return new Logger() {
            public void info(final String message) {
                log.info(message);
            }
            public void warn(final String message) {
                log.warn(message);
            }
        };
    }

    static Logger newStandardOutLogger() {
        return new Logger() {
            public void info(final String message) {
                System.out.println(message);
            }
            public void warn(final String message) {
                System.err.println(message);
            }
        };
    }
}
