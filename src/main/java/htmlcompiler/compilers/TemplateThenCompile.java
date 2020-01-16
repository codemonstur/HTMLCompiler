package htmlcompiler.compilers;

import htmlcompiler.compilers.html.HtmlCompiler;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static htmlcompiler.compilers.FileCompiler.newFileCompilerMap;
import static htmlcompiler.tools.Filenames.toExtension;
import static htmlcompiler.tools.IO.relativize;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.apache.commons.io.FileUtils.listFiles;

public interface TemplateThenCompile {

    void compileTemplate(final File inFile) throws Exception;

    public static TemplateThenCompile newTemplateThenCompile(final MavenProject project, final File inputDir
            , final File outputDir, final boolean replaceExtension, final HtmlCompiler html) {
        final var compilers = newFileCompilerMap(html, project);

        return inFile -> {
            if (inFile == null || !inFile.exists() || !inFile.isFile()) return;

            final String extension = toExtension(inFile, null);
            final var compiler = compilers.get(extension);
            if (compiler == null) return;

            final String output = compiler.compile(inFile);
            final File outputFile = renameFile(inFile, inputDir, outputDir, replaceExtension, compiler.outputExtension());
            Files.writeString(outputFile.toPath(), output, CREATE, TRUNCATE_EXISTING);
        };
    }

    private static File renameFile(final File inputFile, final File inputDir, final File outputDir
            , final boolean replaceExtension, final String replacement) {
        final File outFile = new File(outputDir, extensionize(relativize(inputDir, inputFile), replaceExtension, replacement));
        outFile.getParentFile().mkdirs();
        return outFile;
    }

    private static String extensionize(final String filename, final boolean replaceExtension, final String replacement) {
        return replaceExtension ? filename.substring(0, filename.lastIndexOf('.')) + replacement : filename+replacement;
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
