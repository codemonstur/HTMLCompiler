package htmlcompiler.utils;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.LoggerFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public interface Logger {
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    default void info(final String message) {
        info(message, true);
    }
    default void warn(final String message) {
        warn(message, true);
    }
    default void error(final String message) {
        error(message, true);
    };

    void info(String message, boolean withNewLine);
    void warn(String message, boolean withNewLine);
    void error(String message, boolean withNewLine);

    static Logger newMavenLogger(final Log log) {
        return new Logger() {
            public void info(final String message, final boolean withNewLine) {
                log.info(message);
            }
            public void warn(final String message, final boolean withNewLine) {
                log.warn(message);
            }
            public void error(final String message, final boolean withNewLine) {
                log.error(message);
            }
        };
    }
    static ErrorHandler newVaadinLogger(final Logger logger) {
        return new ErrorHandler() {
            public void warning(CSSParseException e) throws CSSException {
                logger.warn(e.getMessage());
            }
            public void error(CSSParseException e) throws CSSException {
                logger.error(e.getMessage());
            }
            public void fatalError(CSSParseException e) throws CSSException {
                logger.error(e.getMessage());
            }
        };
    }
    static Logger newConsoleLogger() {
        return newLogger(System.out::println, System.err::println, System.err::println);
    }
    static Logger newLogger(final Consumer<String> info, final Consumer<String> warn, final Consumer<String> error) {
        return new Logger() {
            public void info(final String message, final boolean withNewLine) {
                info.accept(withNewLine ? message +"\n": message);
            }
            public void warn(final String message, final boolean withNewLine) {
                warn.accept(withNewLine ? message+"\n" : message);
            }
            public void error(final String message, final boolean withNewLine) {
                error.accept(withNewLine ? message+"\n" : message);
            }
        };
    }

}
