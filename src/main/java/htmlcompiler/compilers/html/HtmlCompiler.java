package htmlcompiler.compilers.html;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.pojos.error.InvalidInput;

import java.io.File;

public interface HtmlCompiler {

    String doctypeCompressCompile(final File file, final String content) throws InvalidInput;
    String compressHtmlCode(final String content);
    String compileHtmlCode(final File file, final String content) throws InvalidInput;

    static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

}
