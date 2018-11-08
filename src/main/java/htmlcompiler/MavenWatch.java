package htmlcompiler;

import htmlcompiler.tools.LogSuppressingMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

import static htmlcompiler.Tasks.compileHTML;
import static htmlcompiler.Tasks.toInputDirectory;
import static htmlcompiler.tools.Logger.newLogger;
import static htmlcompiler.tools.Watcher.watchDirectory;

@Mojo( name = "watch" )
public final class MavenWatch extends LogSuppressingMojo {

    @Parameter( defaultValue = "${project}", readonly = true )
    public MavenProject project;

    @Override
    public void execute() throws MojoFailureException {
        final Log log = getLog();
        try {
            watchDirectory(toInputDirectory(project), () -> compileHTML(newLogger(log::info, log::warn), project));
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

}
