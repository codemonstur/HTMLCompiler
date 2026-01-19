package htmlcompiler.minify;

import static htmlcompiler.utils.HTML.HTML_COMPRESSOR;

public enum HtmlMinifyEngine {

    hazendaz;

    public Minifier toMinifier() {
        return switch (this) {
            case hazendaz -> HTML_COMPRESSOR::compress;
        };
    }

}
