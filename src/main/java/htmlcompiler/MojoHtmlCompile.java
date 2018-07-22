package htmlcompiler;

import htmlcompiler.logging.Logger;
import htmlcompiler.logging.MavenLogger;
import htmlcompiler.model.TemplateSet;
import org.apache.log4j.Level;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static htmlcompiler.compiler.CompileAll.compileTemplates;
import static org.apache.maven.plugins.annotations.LifecyclePhase.COMPILE;

@Mojo( defaultPhase = COMPILE, name = "htmlcompile" )
public final class MojoHtmlCompile extends AbstractMojo {
    static {
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.OFF);
    }

    @Parameter( defaultValue = "${project}", readonly = true )
    public MavenProject project;
    @Parameter( property = "outputdir" )
    public String outputDir;
    @Parameter
    public TemplateSet[] templateSets;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (templateSets == null) {
            templateSets = TemplateSet.newDefaultTemplateSet(project);
        }

        try {
            final Logger log = new MavenLogger(getLog());
            for (final TemplateSet set : templateSets) {
                compileTemplates(log, set, project, outputDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
