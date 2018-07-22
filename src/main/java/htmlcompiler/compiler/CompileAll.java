package htmlcompiler.compiler;

import htmlcompiler.logging.Logger;
import htmlcompiler.model.TemplateSet;
import htmlcompiler.util.Loader;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;

import static htmlcompiler.compiler.HtmlCompiler.DOCTYPE;
import static htmlcompiler.compiler.HtmlCompiler.toTemplateContext;
import static htmlcompiler.util.IO.relativize;
import static java.lang.String.format;

public enum CompileAll {;

    public static void compileTemplates(final Logger log, final TemplateSet templateSet, final MavenProject project
            , final String outputDir) throws MojoFailureException, MojoExecutionException {
        if (templateSet.rootDir == null || !templateSet.rootDir.exists())
            throw new MojoFailureException("Root directory must exist: " + templateSet.rootDir);
        if (!templateSet.rootDir.isDirectory())
            throw new MojoFailureException("Root directory must be a directory");
        if (templateSet.templateDir == null || !templateSet.templateDir.exists())
            throw new MojoFailureException("Template directory must exist: " + templateSet.templateDir);
        if (!templateSet.templateDir.isDirectory())
            throw new MojoFailureException("Template directory must be a directory");

        log.info( format( "Compiling HTML in %s to %s"
                , relativize(project.getBasedir(), templateSet.templateDir)
                , relativize(project.getBasedir(), getOutputDir(templateSet, project, outputDir))
                ) );

        try {
            final Loader loader = new Loader(templateSet.rootDir.getAbsolutePath());
            final JsCompiler js = new JsCompiler(loader, templateSet.compress);
            final CssCompiler css = new CssCompiler(loader, templateSet.compress);
            final HtmlCompiler html = new HtmlCompiler(log, loader, css, js, toTemplateContext(project), templateSet.compress);
            final File fileOutputDir = getOutputDir(templateSet, project, outputDir);

            final Iterator<File> it = FileUtils.iterateFiles(templateSet.templateDir, templateSet.extensions, templateSet.recursive);
            File inFile = null; File outFile;
            while (it.hasNext()) {
                try {
                    inFile = it.next();
                    if (inFile == null || !inFile.isFile() || !inFile.getName().endsWith(".html")) continue;

                    outFile = new File(fileOutputDir, templateSet.rootDir.toPath().relativize(inFile.toPath()).toString());
                    outFile.getParentFile().mkdirs();
                    try (final PrintWriter out = new PrintWriter(outFile)) {
                        out.print(DOCTYPE+html.compile(inFile));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new MojoFailureException("Exception occurred while parsing " + templateSet.rootDir.toPath()
                            .relativize(inFile.toPath()).toString(), e);
                }
            }
        } catch (Exception e) {
            throw new MojoFailureException("Unknown exception occurred", e);
        }
    }

    private static File getOutputDir(final TemplateSet templateset, final MavenProject project, final String outputDir) throws MojoExecutionException {
        if (templateset.outputDir != null) return new File(templateset.outputDir);
        if (outputDir != null) return new File(outputDir);
        if (project != null) return new File(project.getModel().getBuild().getDirectory());
        throw new MojoExecutionException("An output directory must be set");
    }

}
