package htmlcompiler.compilers;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.templates.TemplateEngine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static htmlcompiler.tools.Filenames.toExtension;
import static htmlcompiler.tools.IO.relativize;
import static org.apache.commons.io.FileUtils.listFiles;

public interface TemplateThenCompile {

    void compileTemplate(final File inFile) throws Exception;

    public static TemplateThenCompile newTemplateThenCompile(final Map<String, TemplateEngine> templates, final RenameFile renamer, final HtmlCompiler html) {
        return inFile -> {
            if (inFile == null || !inFile.exists() || !inFile.isFile()) return;

            final TemplateEngine engine = templates.get(toExtension(inFile, null));
            if (engine == null) return;

            try (final PrintWriter out = new PrintWriter(renamer.toOutputFile(inFile))) {
                out.print(html.doctypeCompressCompile(inFile, engine.processTemplate(inFile)));
            }
        };
    }

    public static void compileDirectories(final File inputDir, final TemplateThenCompile ttc, final boolean recursive) throws IOException {
        for (final File inFile : listFiles(inputDir, null, recursive)) {
            try {
                ttc.compileTemplate(inFile);
            } catch (Exception e) {
                throw new IOException("Exception occurred while parsing " + relativize(inputDir, inFile), e);
            }
        }
    }

}
