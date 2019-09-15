package htmlcompiler;

import htmlcompiler.compile.html.HtmlCompiler;
import htmlcompiler.templates.*;
import htmlcompiler.tools.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static htmlcompiler.tools.Filenames.toExtension;
import static htmlcompiler.tools.HTML.DOCTYPE;
import static htmlcompiler.tools.IO.relativize;
import static java.lang.String.format;
import static java.util.Map.entry;
import static org.apache.commons.io.FileUtils.listFiles;

public enum Tasks {;

    private static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void compileHTML(final Logger log, final MavenProject project, final boolean replaceExtension) throws MojoFailureException {
        final File inputDir = toInputDirectory(project);
        final File outputDir = toOutputDirectory(project);

        log.info(format
            ( "[%s] Compiling supported template formats in %s to %s"
            , LocalDateTime.now().format(YYYY_MM_DD_HH_MM_SS)
            , relativize(project.getBasedir(), inputDir)
            , relativize(project.getBasedir(), outputDir)
            ));

        final Map<String, TemplateEngine> engines = Map.ofEntries
            ( entry(".pebble", new Pebble(project))
            , entry(".jade", new Jade4j(project))
            , entry(".md", new Markdown())
            , entry(".hb", new Handlebars(project))
            , entry(".jinjava", new JinJava(project))
            , entry(".twig", new JTwig(project))
            , entry(".hct", new DummyEngine())
            );

        final HtmlCompiler html = new HtmlCompiler(log, inputDir);
        for (final File inFile : listFiles(inputDir, null, true)) {
            final TemplateEngine engine = engines.get(toExtension(inFile, null));
            if (engine == null) continue;

            try {
                try (final PrintWriter out = new PrintWriter(toOutputFile(inputDir, inFile, outputDir, replaceExtension))) {
                    out.print(DOCTYPE+html.compressHtmlCode(html.compileHtmlCode(inFile, engine.processTemplate(inFile))));
                }
            } catch (Exception e) {
                throw new MojoFailureException("Exception occurred while parsing " + relativize(inputDir, inFile), e);
            }
        }
    }

    private static File toOutputFile(final File inputDir, final File inFile, final File outputDir, final boolean replaceExtension) {
        final File outFile = new File(outputDir, extensionize(relativize(inputDir, inFile), replaceExtension));
        outFile.getParentFile().mkdirs();
        return outFile;
    }

    private static String extensionize(final String filename, final boolean replaceExtension) {
        return replaceExtension ? filename.substring(0, filename.lastIndexOf('.')) + ".html" : filename+".html";
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

    public static File toStaticDirectory(final MavenProject project) throws MojoFailureException {
        final File outputDir = new File(project.getBuild().getOutputDirectory(), "wwwroot");
        outputDir.mkdirs();
        if (!outputDir.exists())
            throw new MojoFailureException("Output directory must exist: " + outputDir);
        if (!outputDir.isDirectory())
            throw new MojoFailureException("Output directory must be a directory");
        return outputDir;
    }

}
