package htmlcompiler.compile.js;

import htmlcompiler.tools.IO;

import java.io.File;
import java.io.IOException;

import static htmlcompiler.tools.IO.writeStringToFile;
import static java.io.File.createTempFile;

public enum TypeScriptCompiler {;

    public static String compileTypeScript(final File file) throws IOException {
        final File tempFile = createTempFile("tsc_", ".tmp");
        try {
            Runtime.getRuntime()
                   .exec("tsc --outFile " + tempFile.getAbsolutePath() + " " + file.getAbsolutePath())
                   .waitFor();
            return IO.toString(tempFile);
        } catch (InterruptedException e) {
            throw new IOException("Couldn't wait for tsc", e);
        } finally {
            tempFile.delete();
        }
    }

    public static String compileTypeScript(final File file, final String code) throws IOException {
        final File tempTypescriptFile = writeStringToFile(code, createTempFile(file.getAbsolutePath(), ".tmp"));
        try {
            return compileTypeScript(tempTypescriptFile);
        } finally {
            tempTypescriptFile.delete();
        }
    }

}
