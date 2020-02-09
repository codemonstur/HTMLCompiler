package htmlcompiler.compilers;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.tools.OnlyFileVisitor;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Map;

import static htmlcompiler.compilers.FileCompiler.newFileCompilerMap;
import static htmlcompiler.tools.Filenames.toExtension;
import static java.lang.Integer.MAX_VALUE;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public interface TemplateThenCompile {

    void compileTemplate(final Path inFile) throws Exception;

    public static TemplateThenCompile newTemplateThenCompile(final Path inputDir, final Path outputDir
            , final boolean replaceExtension, final Map<String, String> variables, final HtmlCompiler html) {
        final var compilers = newFileCompilerMap(html, variables);

        return inFile -> {
            if (inFile == null || !isRegularFile(inFile)) return;

            final String extension = toExtension(inFile, null);
            final var compiler = compilers.get(extension);
            if (compiler == null) return;

            final String output = compiler.compile(inFile);
            final Path outputFile = renameFile(inFile, inputDir, outputDir, replaceExtension, compiler.outputExtension());
            Files.writeString(outputFile, output, CREATE, TRUNCATE_EXISTING);
        };
    }

    private static Path renameFile(final Path inputFile, final Path inputDir, final Path outputDir
            , final boolean replaceExtension, final String replacement) {
        final Path outFile = outputDir.resolve(extensionize(inputDir.relativize(inputFile).toString(), replaceExtension, replacement));
        outFile.getParent().toFile().mkdirs();
        return outFile;
    }

    private static String extensionize(final String filename, final boolean replaceExtension, final String replacement) {
        return replaceExtension ? filename.substring(0, filename.lastIndexOf('.')) + replacement : filename+replacement;
    }

    public static void compileDirectories(final Path inputDir, final TemplateThenCompile ttc, final boolean recursive) throws IOException {
        final int maxDepth = recursive ? MAX_VALUE : 1;
        Files.walkFileTree(inputDir, EnumSet.noneOf(FileVisitOption.class), maxDepth, (OnlyFileVisitor) (file, attrs) -> {
            try {
                ttc.compileTemplate(file);
            } catch (Exception e) {
                throw new IOException("Exception occurred while parsing " + inputDir.relativize(file), e);
            }
            return CONTINUE;
        });
    }

}
