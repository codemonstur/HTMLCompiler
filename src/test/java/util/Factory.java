package util;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.compile.CompilerConfig;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.utils.Logger;

import java.util.Map;

import static htmlcompiler.minify.JsMinifyEngine.gcc_simple;

public enum Factory {;

    public static HtmlCompiler newHtmlCompiler() {
        // Keeps test output clean; tests assert on compiled output, not on logs
        final Logger log = Logger.newSilentLogger();
        final LibraryArchive archive = new LibraryArchive();
        final CompilerConfig checks = new CompilerConfig();
        return new HtmlCompiler(log, gcc_simple, archive, Map.of("", checks),
            true, true, true, true,
            true, true, true);
    }

    public static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

}
