package htmlcompiler;

import htmlcompiler.compile.TemplateThenCompile;
import htmlcompiler.tools.LogSuppressingMojo;
import htmlcompiler.tools.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.time.LocalDateTime;

import static htmlcompiler.compile.MavenProjectReader.toInputDirectory;
import static htmlcompiler.compile.MavenProjectReader.toOutputDirectory;
import static htmlcompiler.compile.TemplateThenCompile.RenameFile.defaultRenamer;
import static htmlcompiler.tools.IO.relativize;
import static htmlcompiler.tools.Logger.YYYY_MM_DD_HH_MM_SS;
import static htmlcompiler.tools.Logger.newLogger;
import static java.lang.String.format;
import static org.apache.commons.io.FileUtils.listFiles;
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
        final Logger log = newLogger(getLog());

        final File inputDir = toInputDirectory(project);
        final File outputDir = toOutputDirectory(project);

        log.info(format
            ( "[%s] Compiling supported template formats in %s to %s"
            , LocalDateTime.now().format(YYYY_MM_DD_HH_MM_SS)
            , relativize(project.getBasedir(), inputDir)
            , relativize(project.getBasedir(), outputDir)
            ));

        final var ttc = new TemplateThenCompile(log, defaultRenamer(inputDir, outputDir, replaceExtension), project);
        for (final File inFile : listFiles(inputDir, null, true)) {
            try {
                ttc.compileTemplate(inFile);
            } catch (Exception e) {
                throw new MojoFailureException("Exception occurred while parsing " + relativize(inputDir, inFile), e);
            }
        }
    }

}