package htmlcompiler.compilers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.tools.IO.findBinaryInPath;
import static htmlcompiler.tools.IO.newTempFileWithContent;

public interface CodeCompiler {

    String compileCode(String code, Path parent) throws Exception;
    String compileCode(Path style) throws Exception;

    public static CodeCompiler newNopCompiler() {
        return new CodeCompiler() {
            public String compileCode(String code, Path parent) {
                return code;
            }
            public String compileCode(Path script) throws IOException {
                return Files.readString(script);
            }
        };
    }

    public interface CompileArgumentsSupplier {
        String newCompileArguments(Path outputFile, Path inputFile);
    }

    public static CodeCompiler newExternalToolCompiler(final String toolName, final String scriptExtension
            , final CompileArgumentsSupplier supplier) {
        return newExternalToolCompiler(toolName, scriptExtension, false, scriptExtension, supplier);
    }
    public static CodeCompiler newExternalToolCompiler(final String toolName, final String scriptExtension
            , final boolean deleteFirst, final String tempFileExtension, final CompileArgumentsSupplier supplier) {
        final Path toolLocation = findBinaryInPath(toolName, null);

        return new CodeCompiler() {
            public String compileCode(String code, Path owner) throws IOException {
                final Path tempFile = newTempFileWithContent("hc_in_", scriptExtension, owner.getParent(), code);
                try {
                    return compileCode(tempFile);
                } finally {
                    Files.delete(tempFile);
                }
            }

            public String compileCode(Path script) throws IOException {
                if (toolLocation == null) throw new FileNotFoundException("Could not find binary " + toolName + " in PATH");

                final Path tempFile = Files.createTempFile(toolName + "_", tempFileExtension);
                if (deleteFirst) Files.delete(tempFile);
                try {
                    final String cmd = toolLocation.toAbsolutePath() + " " + supplier.newCompileArguments(tempFile, script);
                    Runtime.getRuntime().exec(cmd).waitFor();
                    return Files.readString(tempFile);
                } catch (InterruptedException e) {
                    throw new IOException("Couldn't wait for " + toolName, e);
                } finally {
                    if (!deleteFirst) Files.delete(tempFile);
                }
            }
        };
    }

}
