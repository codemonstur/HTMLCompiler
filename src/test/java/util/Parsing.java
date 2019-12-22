package util;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.tools.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public enum Parsing {;

    public static String compileFile(final HtmlCompiler compiler, final String filename)
            throws InvalidInput, IOException {
        final File file = new File(filename);
        try (final InputStream in = new FileInputStream(file)) {
            final String content = IO.toString(in);
            return compiler.doctypeCompressCompile(file, content);
        }
    }

}
