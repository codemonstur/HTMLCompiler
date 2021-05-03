package util;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.compile.CompilerConfig;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tools.Logger;

import java.util.Map;

import static htmlcompiler.tools.Logger.newLogger;

public enum Factory {;

    public static HtmlCompiler newHtmlCompiler() {
        final Logger log = newLogger(System.out::print, System.out::print);
        final LibraryArchive archive = new LibraryArchive();
        final CompilerConfig checks = new CompilerConfig();
        return new HtmlCompiler(log, archive, Map.of("", checks));
    }

    public static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

}
