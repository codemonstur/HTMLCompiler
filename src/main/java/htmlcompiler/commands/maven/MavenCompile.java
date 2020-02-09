package htmlcompiler.commands.maven;

import htmlcompiler.commands.Compile;
import htmlcompiler.commands.Compile.CompileCommandConfig;
import htmlcompiler.pojos.compile.CompilerType;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static htmlcompiler.tools.App.buildMavenTask;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_RESOURCES;

@Mojo( defaultPhase = GENERATE_RESOURCES, name = "compile" )
public final class MavenCompile extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Parameter(defaultValue = "true")
    public boolean enabled;
    @Parameter(defaultValue = "true")
    public boolean recursive;
    @Parameter(defaultValue = "true")
    public boolean replaceExtension;
    @Parameter(defaultValue = "jsoup")
    public CompilerType type;
    @Parameter(defaultValue = "src/main/websrc/validation.json")
    public String validation;

    public void execute() throws MojoFailureException {
        if (!enabled) return;
        buildMavenTask(this, log -> Compile.executeCompile(log, newCompileConfig()));
    }

    private CompileCommandConfig newCompileConfig() throws MojoFailureException {
        final var config = new CompileCommandConfig();
        config.inputDir = MavenProjectReader.toInputDirectory(project);
        config.outputDir = MavenProjectReader.toOutputDirectory(project);
        config.variables = MavenProjectReader.newTemplateContext(project);
        config.baseDir = project.getBasedir().toPath();
        config.recursive = recursive;
        config.replaceExtension = replaceExtension;
        config.type = type;
        config.validation = validation;
        return config;
    }

}