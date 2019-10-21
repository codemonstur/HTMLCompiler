package htmlcompiler.tools;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

import static htmlcompiler.tools.Logger.newLogger;

public enum App {;

    public interface MavenTask {
        void build(Logger log) throws Exception;
    }

    public static void buildMavenTask(final AbstractMojo mojo, final MavenTask task) throws MojoFailureException {
        try {
            final Logger log = newLogger(mojo.getLog());
            task.build(log);
        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }
}
