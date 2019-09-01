package htmlcompiler;

import htmlcompiler.tools.LogSuppressingMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static htmlcompiler.Tasks.compileHTML;
import static htmlcompiler.tools.Logger.newLogger;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_RESOURCES;

@Mojo( defaultPhase = GENERATE_RESOURCES, name = "compile" )
public final class MavenCompile extends LogSuppressingMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Parameter(defaultValue = "true")
    public boolean enabled;

    @Parameter(defaultValue = "true")
    public boolean replaceExtension;

    public void execute() throws MojoFailureException {
        if (!enabled) return;

        final Log log = getLog();
        compileHTML(newLogger(log::info, log::warn), project, replaceExtension);
    }

}