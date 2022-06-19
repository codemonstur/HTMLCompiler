package htmlcompiler.pojos.compile;

import htmlcompiler.compilers.Compressor;
import htmlcompiler.compilers.JsCompiler;
import htmlcompiler.tools.Logger;

public enum JsCompressionType {
    gcc_simple, gcc_bundle, gcc_whitespace, gcc_advanced, yui;

    public Compressor toCompressor(final Logger log) {
        return switch (this) {
            case gcc_simple -> JsCompiler::compressJsWithGccSimple;
            case gcc_bundle -> JsCompiler::compressJsWithGccBundle;
            case gcc_whitespace -> JsCompiler::compressJsWithGccWhitespace;
            case gcc_advanced -> JsCompiler::compressJsWithGccAdvanced;
            case yui -> JsCompiler.newCompressJsWithYui(log);
        };
    }
}
