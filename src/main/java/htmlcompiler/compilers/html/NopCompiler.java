package htmlcompiler.compilers.html;

import htmlcompiler.pojos.compile.Config;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tools.Logger;

import java.nio.file.Path;

public final class NopCompiler implements HtmlCompiler {

    public NopCompiler(final Logger logger, final LibraryArchive libraryArchive, final Config stringBooleanMap) {}

    public String doctypeCompressCompile(final Path file, final String content) {
        return content;
    }

    public String compressHtmlCode(final String content) {
        return content;
    }

    public String compileHtmlCode(final Path file, final String content) {
        return content;
    }

}
