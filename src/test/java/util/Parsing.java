package util;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum Parsing {;

    public static String compileFile(final HtmlCompiler compiler, final String filename)
            throws InvalidInput, IOException {
        final Path file = Paths.get(filename);
        return compiler.doctypeCompressCompile(file, Files.readString(file));
    }

}
