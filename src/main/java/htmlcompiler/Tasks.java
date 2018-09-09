package htmlcompiler;

import htmlcompiler.compile.html.HtmlCompiler;
import htmlcompiler.tools.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static htmlcompiler.compile.html.HtmlCompiler.newDefaultTemplateContext;
import static htmlcompiler.tools.HTML.DOCTYPE;
import static htmlcompiler.tools.IO.relativize;
import static java.lang.String.format;
import static org.apache.commons.io.FileUtils.listFiles;

public enum Tasks {;

    private static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void compileHTML(final Logger log, final MavenProject project) throws MojoFailureException {
        final File inputDir = toInputDirectory(project);
        final File outputDir = toOutputDirectory(project);

        log.info( format( "[%s] Compiling HTML in %s to %s"
                , LocalDateTime.now().format(YYYY_MM_DD_HH_MM_SS)
                , relativize(project.getBasedir(), inputDir)
                , relativize(project.getBasedir(), outputDir)
                ));

        final HtmlCompiler html = new HtmlCompiler(log, inputDir, newDefaultTemplateContext(project));
        for (final File inFile : listFiles(inputDir, null, true)) {
            if (!isHtmlFile(inFile)) continue;

            try (final PrintWriter out = new PrintWriter(toOutputFile(inputDir, inFile, outputDir))) {
                out.print(DOCTYPE+html.compileHtmlFile(inFile));
            } catch (Exception e) {
                throw new MojoFailureException("Exception occurred while parsing " + relativize(inputDir, inFile), e);
            }
        }
    }

    private static File toOutputFile(final File inputDir, final File inFile, final File outputDir) {
        final File outFile = new File(outputDir, relativize(inputDir, inFile));
        outFile.getParentFile().mkdirs();
        return outFile;
    }

    private static boolean isHtmlFile(final File inFile) {
        return inFile != null && inFile.isFile() && inFile.getName().endsWith(".html");
    }

    public static File toInputDirectory(final MavenProject project) throws MojoFailureException {
        final File inputDir = new File(project.getBasedir(), "src/main/websrc");
        if (!inputDir.exists())
            throw new MojoFailureException("Input directory must exist: " + inputDir);
        if (!inputDir.isDirectory())
            throw new MojoFailureException("Input directory must be a directory");
        return inputDir;
    }

    public static File toOutputDirectory(final MavenProject project) throws MojoFailureException {
        final File outputDir = new File(project.getBuild().getOutputDirectory(), "webbin");
        outputDir.mkdirs();
        if (!outputDir.exists())
            throw new MojoFailureException("Output directory must exist: " + outputDir);
        if (!outputDir.isDirectory())
            throw new MojoFailureException("Output directory must be a directory");
        return outputDir;
    }

}
