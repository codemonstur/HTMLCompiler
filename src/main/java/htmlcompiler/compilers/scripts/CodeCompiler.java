package htmlcompiler.compilers.scripts;

import htmlcompiler.tools.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import static htmlcompiler.tools.IO.findBinaryInPath;
import static htmlcompiler.tools.IO.newTempFileWithContent;
import static java.io.File.createTempFile;

public interface CodeCompiler {

    String compileCode(String code, File parent) throws Exception;
    String compileCode(File style) throws Exception;

    public static CodeCompiler newNopCompiler() {
        return new CodeCompiler() {
            public String compileCode(String code, File parent) {
                return code;
            }
            public String compileCode(File script) throws IOException {
                return IO.toString(script);
            }
        };
    }

    public interface CompileArgumentsSupplier {
        String newCompileArguments(File outputFile, File inputFile);
    }

    public static CodeCompiler newExternalToolCompiler(final String toolName, final String scriptExtension
            , final CompileArgumentsSupplier supplier) {
        return newExternalToolCompiler(toolName, scriptExtension, false, scriptExtension, supplier);
    }
    public static CodeCompiler newExternalToolCompiler(final String toolName, final String scriptExtension
            , final boolean deleteFirst, final String tempFileExtension, final CompileArgumentsSupplier supplier) {
        final Path toolLocation = findBinaryInPath(toolName, null);

        return new CodeCompiler() {
            public String compileCode(String code, File owner) throws IOException {
                final File tempFile = newTempFileWithContent("hc_in_", scriptExtension, owner.getParentFile(), code);
                try {
                    return compileCode(tempFile);
                } finally {
                    tempFile.delete();
                }
            }

            public String compileCode(File script) throws IOException {
                if (toolLocation == null) throw new FileNotFoundException("Could not find binary " + toolName + " in PATH");

                final File tempFile = createTempFile(toolName + "_", tempFileExtension);
                if (deleteFirst) tempFile.delete();
                try {
                    final String cmd = toolLocation.toAbsolutePath() + " " + supplier.newCompileArguments(tempFile, script);
                    Runtime.getRuntime().exec(cmd).waitFor();
                    return IO.toString(tempFile);
                } catch (InterruptedException e) {
                    throw new IOException("Couldn't wait for " + toolName, e);
                } finally {
                    if (!deleteFirst) tempFile.delete();
                }
            }
        };
    }

}
