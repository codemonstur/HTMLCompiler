package htmlcompiler.compilers.js;

import htmlcompiler.tools.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.tools.IO.findBinaryInPath;
import static java.io.File.createTempFile;
import static java.nio.file.StandardOpenOption.CREATE;

public enum TypeScriptCompiler {;

    private static Path TYPESCRIPT_COMPILER;
    private static Path getTypescriptCompiler() throws FileNotFoundException {
        if (TYPESCRIPT_COMPILER == null)
            TYPESCRIPT_COMPILER = findBinaryInPath("tsc");
        return TYPESCRIPT_COMPILER;
    }

    public static ScriptCompiler newTypescriptCompiler() {
        return new ScriptCompiler() {
            public String compileScript(String code, File parent) throws IOException {
                return compileScript(code, parent);
            }
            public String compileScript(File script) throws IOException {
                return compileTypeScript(script);
            }
        };
    }
    public static String compileTypeScript(final String source, final File file) throws IOException {
        final File tempFile = createTempFile("hc_in_", ".tsc", file.getParentFile());
        try {
            Files.writeString(tempFile.toPath(), source, CREATE);
            return compileTypeScript(tempFile);
        } finally {
            tempFile.delete();
        }
    }

    public static String compileTypeScript(final File file) throws IOException {
        final Path tsc = getTypescriptCompiler();
        final File tempFile = createTempFile("tsc_", ".tmp");
        try {
            Runtime.getRuntime()
                   .exec(tsc.toAbsolutePath() + " --outFile " + tempFile.getAbsolutePath() + " " + file.getAbsolutePath())
                   .waitFor();
            return IO.toString(tempFile);
        } catch (InterruptedException e) {
            throw new IOException("Couldn't wait for tsc", e);
        } finally {
            tempFile.delete();
        }
    }

}
