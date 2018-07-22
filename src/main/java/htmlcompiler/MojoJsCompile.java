package htmlcompiler;

import htmlcompiler.compiler.JsCompiler;
import htmlcompiler.logging.Logger;
import htmlcompiler.logging.MavenLogger;
import htmlcompiler.model.JavascriptSet;
import htmlcompiler.util.Loader;
import org.apache.log4j.Level;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintWriter;

import static htmlcompiler.util.IO.relativize;
import static org.apache.maven.plugins.annotations.LifecyclePhase.COMPILE;

@org.apache.maven.plugins.annotations.Mojo( defaultPhase = COMPILE, name = "jscompile" )
public final class MojoJsCompile extends AbstractMojo {
    static {
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.OFF);
    }

    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;
    @Parameter( defaultValue = "target/classes/wwwroot", property = "outputDir" )
    private String outputDir;
    @Parameter( defaultValue = "src/main/websrc/html", property = "rootDir" )
    private String rootDir;

    @Parameter
    private JavascriptSet[] jsfiles;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Logger log = new MavenLogger(getLog());
        for (final JavascriptSet jsSet : jsfiles) {
            compileJs(log, jsSet);
        }
    }

    private void compileJs(final Logger log, final JavascriptSet jsSet) throws MojoExecutionException, MojoFailureException {
        final File rootDir = getRootDir(jsSet, project);
        if (rootDir == null || !rootDir.exists() || !rootDir.isDirectory())
            throw new MojoFailureException("Root directory must exist and be a directory: " + relativize(project.getBasedir(), rootDir));
        if (jsSet.jsFile == null || !jsSet.jsFile.exists() || !jsSet.jsFile.isFile())
            throw new MojoFailureException("JS file must exist and be a file: " + relativize(project.getBasedir(), jsSet.jsFile));

        final File outFile = new File(getOutputDir(jsSet, project), relativize(getRootDir(jsSet, project), jsSet.jsFile));
        if (outFile.exists())
            throw new MojoFailureException("Output location must not exist: " + relativize(project.getBasedir(), outFile));

        log.info( String.format( "Compiling CSS in %s to %s"
                , relativize(project.getBasedir(), jsSet.jsFile)
                , relativize(project.getBasedir(), getOutputDir(jsSet, project))
        ) );

        final Loader loader = new Loader(getRootDir(jsSet, project).getAbsolutePath());
        final JsCompiler js = new JsCompiler(loader, jsSet.compress);
        final String jsLocation = relativize(rootDir, jsSet.jsFile);

        outFile.getParentFile().mkdirs();

        try (final PrintWriter out = new PrintWriter(outFile)) {
            out.print(js.compile(new File(rootDir, jsLocation)));
        } catch (Exception e) {
            throw new MojoFailureException("Error during CSS compilation", e);
        }
    }

    private File getRootDir(final JavascriptSet jsSet, final MavenProject project) throws MojoExecutionException {
        if (jsSet.rootDir != null) return jsSet.rootDir;
        if (outputDir != null) return new File(outputDir);
        if (project != null) return project.getBasedir();
        throw new MojoExecutionException("An output directory must be set");
    }
    private File getOutputDir(final JavascriptSet jsSet, final MavenProject project) throws MojoExecutionException {
        if (jsSet.outputDir != null) return new File(jsSet.outputDir);
        if (outputDir != null) return new File(outputDir);
        if (project != null) return new File(project.getModel().getBuild().getDirectory());
        throw new MojoExecutionException("An output directory must be set");
    }
}
