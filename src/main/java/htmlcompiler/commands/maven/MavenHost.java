package htmlcompiler.commands.maven;

import htmlcompiler.commands.Host;
import htmlcompiler.commands.Host.HostCommandConfig;
import htmlcompiler.pojos.compile.CompilerType;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.nio.file.Path;

import static htmlcompiler.tools.App.buildMavenTask;

@Mojo( name = "host" )
public final class MavenHost extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Parameter(defaultValue = "8080")
    public int port;
    @Parameter(defaultValue = "true")
    public boolean replaceExtension;
    @Parameter(defaultValue = "src/main/webinc")
    public String watchedDirectories;
    @Parameter(defaultValue = "true")
    public boolean requestApiEnabled;
    @Parameter(defaultValue = "src/main/webcnf/requests.json")
    public String requestApiSpecification;
    @Parameter(defaultValue = "src/main/webcnf/validation.json")
    public String validation;
    @Parameter(defaultValue = "jsoup")
    public CompilerType type;

    @Override
    public void execute() throws MojoFailureException {
        buildMavenTask(this, log -> Host.executeHost(log, newHostConfig()));
    }

    private HostCommandConfig newHostConfig() throws MojoFailureException {
        final var config = new HostCommandConfig();
        config.inputDir = MavenProjectReader.toInputDirectory(project);
        config.outputDir = MavenProjectReader.toOutputDirectory(project);
        config.variables = MavenProjectReader.newTemplateContext(project);
        config.baseDir = project.getBasedir().toPath();
        config.hostedPaths = new Path[] { MavenProjectReader.toStaticDirectory(project), MavenProjectReader.toOutputDirectory(project) };
        config.port = port;
        config.replaceExtension = replaceExtension;
        config.watchedDirectories = watchedDirectories;
        config.requestApiEnabled = requestApiEnabled;
        config.requestApiSpecification = requestApiSpecification;
        config.validation = validation;
        config.type = type;
        return config;
    }

}
