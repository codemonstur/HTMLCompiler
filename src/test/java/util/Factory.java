package util;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.compilers.html.CodelibsCompiler;
import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.compilers.html.HtmlUnitCompiler;
import htmlcompiler.tools.Logger;

import java.io.IOException;
import java.util.stream.Stream;

import static htmlcompiler.tools.Logger.newLogger;

public enum Factory {;

    public static JsoupCompiler newHtmlCompiler2() throws IOException {
        final Logger log = newLogger(System.out::println, System.out::println);
        return new JsoupCompiler(log);
    }

    public static Stream<HtmlCompiler> provideCompilers() throws IOException {
        final Logger log = newLogger(System.out::println, System.out::println);
        return Stream.of(new JsoupCompiler(log), new HtmlUnitCompiler(log), new CodelibsCompiler(log));
    }

    public static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

}
