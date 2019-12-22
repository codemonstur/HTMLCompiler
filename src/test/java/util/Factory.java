package util;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.model.CompilerType;
import htmlcompiler.tools.Logger;

import java.util.stream.Stream;

import static htmlcompiler.tools.Logger.newLogger;

public enum Factory {;

    public static Stream<HtmlCompiler> provideCompilers() {
        final Logger log = newLogger(System.out::println, System.out::println);
        return Stream.of(CompilerType.values()).map(type -> type.newHtmlCompiler(log));
    }

    public static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

}
