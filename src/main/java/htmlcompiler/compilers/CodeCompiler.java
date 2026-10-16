package htmlcompiler.compilers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static htmlcompiler.utils.IO.findBinaryInPath;
import static htmlcompiler.utils.IO.newTempFileWithContent;

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
        String[] newCompileArguments(Path outputFile, Path inputFile);
    }
    public interface StdoutCompileArgumentsSupplier {
        String[] newCompileArguments(Path inputFile);
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
                    Files.deleteIfExists(tempFile);
                }
            }

            public String compileCode(Path script) throws IOException {
                if (toolLocation == null) throw new FileNotFoundException("Could not find binary " + toolName + " in PATH");

                final Path tempFile = Files.createTempFile(toolName + "_", tempFileExtension);
                if (deleteFirst) Files.delete(tempFile);
                try {
                    final var command = new ArrayList<String>();
                    command.add(toolLocation.toAbsolutePath().toString());
                    command.addAll(List.of(supplier.newCompileArguments(tempFile, script)));

                    // The tool writes its output to a file, so both streams go to the console:
                    // nothing is buffered here, which rules out a full pipe deadlock, and a
                    // failing tool reports its own error message
                    final int exitCode = new ProcessBuilder(command)
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start().waitFor();
                    if (exitCode != 0)
                        throw new IOException(toolName + " failed with exit code " + exitCode + " compiling " + script);
                    if (deleteFirst && !Files.exists(tempFile))
                        throw new IOException(toolName + " completed without producing an output file for " + script);
                    return Files.readString(tempFile);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Couldn't wait for " + toolName, e);
                } finally {
                    Files.deleteIfExists(tempFile);
                }
            }
        };
    }

    public static CodeCompiler newStdoutToolCompiler(final String toolName, final String scriptExtension
            , final StdoutCompileArgumentsSupplier supplier) {
        final Path toolLocation = findBinaryInPath(toolName, null);

        return new CodeCompiler() {
            public String compileCode(String code, Path owner) throws IOException {
                final Path tempFile = newTempFileWithContent("hc_in_", scriptExtension, owner.getParent(), code);
                try {
                    return compileCode(tempFile);
                } finally {
                    Files.deleteIfExists(tempFile);
                }
            }

            public String compileCode(Path script) throws IOException {
                if (toolLocation == null) throw new FileNotFoundException("Could not find binary " + toolName + " in PATH");

                final var command = new ArrayList<String>();
                command.add(toolLocation.toAbsolutePath().toString());
                command.addAll(List.of(supplier.newCompileArguments(script)));
                try {
                    final Process process = new ProcessBuilder(command)
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start();
                    // Only stdout is piped and it is drained before waiting, so no deadlock can occur
                    final String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    final int exitCode = process.waitFor();
                    if (exitCode != 0)
                        throw new IOException(toolName + " failed with exit code " + exitCode + " compiling " + script);
                    return output;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Couldn't wait for " + toolName, e);
                }
            }
        };
    }

}
