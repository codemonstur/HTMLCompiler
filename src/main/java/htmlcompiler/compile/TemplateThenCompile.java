package htmlcompiler.compile;

import htmlcompiler.templates.*;
import htmlcompiler.tools.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

import static htmlcompiler.tools.Filenames.toExtension;
import static htmlcompiler.tools.HTML.DOCTYPE;
import static htmlcompiler.tools.IO.relativize;
import static java.util.Map.entry;

public final class TemplateThenCompile {

    public interface RenameFile {
        File toOutputFile(File inputFile);

        static RenameFile defaultRenamer(final File inputDir, final File outputDir, final boolean replaceExtension) {
            return inputFile -> {
                final File outFile = new File(outputDir, extensionize(relativize(inputDir, inputFile), replaceExtension));
                outFile.getParentFile().mkdirs();
                return outFile;
            };
        }

        private static String extensionize(final String filename, final boolean replaceExtension) {
            return replaceExtension ? filename.substring(0, filename.lastIndexOf('.')) + ".html" : filename+".html";
        }
    }

    private final Map<String, TemplateEngine> templates;
    private final HtmlCompiler html;
    private final RenameFile renamer;

    public TemplateThenCompile(final Logger log, final RenameFile renamer, final MavenProject project)
            throws MojoFailureException {
        this.templates = Map.ofEntries
            ( entry(".pebble", new Pebble(project))
            , entry(".jade", new Jade4j(project))
            , entry(".md", new Markdown())
            , entry(".hb", new Handlebars(project))
            , entry(".jinjava", new JinJava(project))
            , entry(".twig", new JTwig(project))
            , entry(".mustache", new Mustache(project))
            , entry(".thymeleaf", new Thymeleaf(project))
            , entry(".hct", new DummyEngine())
            );
        this.html = new HtmlCompiler(log);
        this.renamer = renamer;
    }

    public void compileTemplate(final File inFile) throws Exception {
        if (inFile == null || !inFile.exists() || !inFile.isFile()) return;

        final TemplateEngine engine = templates.get(toExtension(inFile, null));
        if (engine == null) return;

        try (final PrintWriter out = new PrintWriter(renamer.toOutputFile(inFile))) {
            out.print(DOCTYPE+html.compressHtmlCode(html.compileHtmlCode(inFile, engine.processTemplate(inFile))));
        }
    }

}
