package htmlcompiler;

import htmlcompiler.compiler.CssCompiler;
import htmlcompiler.logging.Logger;
import htmlcompiler.logging.MavenLogger;
import htmlcompiler.model.StylesheetSet;
import htmlcompiler.util.Loader;
import org.apache.log4j.Level;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintWriter;

import static htmlcompiler.util.IO.relativize;
import static org.apache.maven.plugins.annotations.LifecyclePhase.COMPILE;

@Mojo( defaultPhase = COMPILE, name = "csscompile" )
public final class MojoCssCompile extends AbstractMojo {
    static {
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.OFF);
    }

    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;
    @Parameter( property = "outputDir" )
    private String outputDir;
    @Parameter( property = "rootDir" )
    private String rootDir;
    @Parameter
    private StylesheetSet[] cssfiles;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Logger log = new MavenLogger(getLog());
        for (final StylesheetSet cssSet : cssfiles) {
            compileCss(log, cssSet);
        }
    }

    private void compileCss(final Logger log, final StylesheetSet styleSet) throws MojoExecutionException, MojoFailureException {
        final File rootDir = getRootDir(styleSet, project);
        if (rootDir == null || !rootDir.exists() || !rootDir.isDirectory())
            throw new MojoFailureException("Root directory must exist and be a directory: " + relativize(project.getBasedir(), rootDir));
        if (styleSet.cssFile == null || !styleSet.cssFile.exists() || !styleSet.cssFile.isFile())
            throw new MojoFailureException("CSS file must exist and be a file: " + relativize(project.getBasedir(), styleSet.cssFile));

        final File outFile = new File(getOutputDir(styleSet, project), relativize(getRootDir(styleSet, project), styleSet.cssFile));
        if (outFile.exists())
            throw new MojoFailureException("Output location must not exist: " + relativize(project.getBasedir(), outFile));

        log.info( String.format( "Compiling CSS in %s to %s"
                , relativize(project.getBasedir(), styleSet.cssFile)
                , relativize(project.getBasedir(), getOutputDir(styleSet, project))
        ) );

        final Loader loader = new Loader(getRootDir(styleSet, project).getAbsolutePath());
        final CssCompiler css = new CssCompiler(loader, styleSet.compress);
        final String cssLocation = relativize(getRootDir(styleSet, project), styleSet.cssFile);

        outFile.getParentFile().mkdirs();

        try (final PrintWriter out = new PrintWriter(outFile)) {
            out.print(css.compile(cssLocation));
        } catch (Exception e) {
            throw new MojoFailureException("Error during CSS compilation", e);
        }
    }

    private File getRootDir(final StylesheetSet styleSet, final MavenProject project) throws MojoExecutionException {
        if (styleSet.rootDir != null) return styleSet.rootDir;
        if (outputDir != null) return new File(outputDir);
        if (project != null) return project.getBasedir();
        throw new MojoExecutionException("An output directory must be set");
    }
    private File getOutputDir(final StylesheetSet styleSet, final MavenProject project) throws MojoExecutionException {
        if (styleSet.outputDir != null) return new File(styleSet.outputDir);
        if (outputDir != null) return new File(outputDir);
        if (project != null) return new File(project.getModel().getBuild().getDirectory());
        throw new MojoExecutionException("An output directory must be set");
    }
}
