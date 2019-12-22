package htmlcompiler.compilers.html;

import htmlcompiler.error.InvalidInput;

import java.io.File;

public interface HtmlCompiler {

    String doctypeCompressCompile(final File file, final String content) throws InvalidInput;
    String compressHtmlCode(final String content);
    String compileHtmlCode(final File file, final String content) throws InvalidInput;

}
