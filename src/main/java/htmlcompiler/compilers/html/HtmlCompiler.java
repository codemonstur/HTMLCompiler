package htmlcompiler.compilers.html;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.pojos.error.InvalidInput;

import java.nio.file.Path;

public interface HtmlCompiler {

    String doctypeCompressCompile(final Path file, final String content) throws InvalidInput;
    String compressHtmlCode(final String content);
    String compileHtmlCode(final Path file, final String content) throws InvalidInput;

    static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

}
