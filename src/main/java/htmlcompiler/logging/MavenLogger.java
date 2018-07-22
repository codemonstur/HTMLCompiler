package htmlcompiler.logging;

import org.apache.maven.plugin.logging.Log;

import static java.util.Objects.requireNonNull;

public final class MavenLogger implements Logger {

    private final Log log;
    public MavenLogger(final Log log) {
        this.log = requireNonNull(log, "Maven log must not be null");
    }

    @Override
    public void info(final String message) {
        this.log.info(message);
    }

    @Override
    public void warn(final String message) {
        this.log.warn(message);
    }
}
