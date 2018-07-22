package htmlcompiler.tools;

import org.apache.log4j.Level;
import org.apache.maven.plugin.AbstractMojo;

public abstract class LogSuppressingMojo extends AbstractMojo {
    static {
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.OFF);
    }
}
