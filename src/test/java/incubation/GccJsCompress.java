package incubation;

import com.googlecode.htmlcompressor.compressor.ClosureJavaScriptCompressor;

import static com.google.javascript.jscomp.CompilationLevel.ADVANCED_OPTIMIZATIONS;

public class GccJsCompress {

    public static void main(final String... args) {
        final var compress = new ClosureJavaScriptCompressor(ADVANCED_OPTIMIZATIONS);
        compress.compress(null);
    }

}
