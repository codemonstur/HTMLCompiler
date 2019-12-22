package htmlcompiler;

import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.model.CompilerType;
import htmlcompiler.tools.LogSuppressingMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.time.LocalDateTime;

import static htmlcompiler.compilers.MavenProjectReader.toInputDirectory;
import static htmlcompiler.compilers.MavenProjectReader.toOutputDirectory;
import static htmlcompiler.compilers.RenameFile.defaultRenamer;
import static htmlcompiler.compilers.TemplateThenCompile.compileDirectories;
import static htmlcompiler.compilers.TemplateThenCompile.newTemplateThenCompile;
import static htmlcompiler.templates.TemplateEngine.newExtensionToEngineMap;
import static htmlcompiler.tools.App.buildMavenTask;
import static htmlcompiler.tools.IO.relativize;
import static htmlcompiler.tools.Logger.YYYY_MM_DD_HH_MM_SS;
import static java.lang.String.format;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_RESOURCES;

@Mojo( defaultPhase = GENERATE_RESOURCES, name = "compile" )
public final class MavenCompile extends LogSuppressingMojo {

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

    public void execute() throws MojoFailureException {
        if (!enabled) return;

        buildMavenTask(this, log -> {
            final var inputDir = toInputDirectory(project);
            final var outputDir = toOutputDirectory(project);

            final var templates = newExtensionToEngineMap(project);
            final var html = type.newHtmlCompiler(log);
            final var ttc = newTemplateThenCompile(templates, defaultRenamer(inputDir, outputDir, replaceExtension), html);

            log.info(format
                ( "[%s] Compiling supported template formats in %s to %s"
                , LocalDateTime.now().format(YYYY_MM_DD_HH_MM_SS)
                , relativize(project.getBasedir(), inputDir)
                , relativize(project.getBasedir(), outputDir)
                ));

            compileDirectories(inputDir, ttc, recursive);
        });
    }

}