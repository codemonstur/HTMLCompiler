package htmlcompiler;

import htmlcompiler.tools.LogSuppressingMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static htmlcompiler.Tasks.compileHTML;
import static htmlcompiler.tools.Logger.toLogger;
import static org.apache.maven.plugins.annotations.LifecyclePhase.COMPILE;

@Mojo( defaultPhase = COMPILE, name = "htmlcompile" )
public final class MavenHtmlCompile extends LogSuppressingMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    public void execute() throws MojoFailureException {
        compileHTML(toLogger(getLog()), project);
    }

}