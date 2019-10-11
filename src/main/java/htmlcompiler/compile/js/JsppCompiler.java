package htmlcompiler.compile.js;

import htmlcompiler.tools.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.tools.IO.findBinaryInPath;
import static java.io.File.createTempFile;
import static java.nio.file.StandardOpenOption.CREATE;

public enum JsppCompiler {;

    public static ScriptCompiler newJsppCompiler() {
        return new ScriptCompiler() {
            public String compileScript(String code, File parent) throws IOException {
                return compileJspp(code, parent);
            }
            public String compileScript(File script) throws IOException {
                return compileJspp(script);
            }
        };
    }

    private static Path JSPP_COMPILER;
    private static Path getJsppCompiler() throws FileNotFoundException {
        if (JSPP_COMPILER == null)
            JSPP_COMPILER = findBinaryInPath("js++");
        return JSPP_COMPILER;
    }

    public static String compileJspp(final String source, final File htmlFile) throws IOException {
        final File tempFile = createTempFile("hc_is_", ".jspp", htmlFile.getParentFile());
        try {
            Files.writeString(tempFile.toPath(), source, CREATE);
            return compileJspp(tempFile);
        } finally {
            tempFile.delete();
        }
    }

    public static String compileJspp(final File jsppFile) throws IOException {
        final Path jspp = getJsppCompiler();
        final File tempFile = createTempFile("tsc_", ".tmp");
        try {
            Runtime.getRuntime()
                .exec(jspp.toAbsolutePath() + " " + jsppFile.getAbsolutePath() + " -o " + tempFile.getAbsolutePath())
                .waitFor();
            return IO.toString(tempFile);
        } catch (InterruptedException e) {
            throw new IOException("Interrupted while waiting for js++", e);
        } finally {
            tempFile.delete();
        }
    }

}
