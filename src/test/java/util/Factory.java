package util;

import com.google.gson.Gson;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.pojos.compile.CompilerType;
import htmlcompiler.tools.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static htmlcompiler.tools.Logger.newLogger;

public enum Factory {;

    public static Stream<HtmlCompiler> provideCompilers() {
        final Gson gson = new Gson();
        final Logger log = newLogger(System.out::println, System.out::println);
        final LibraryArchive archive = new LibraryArchive(gson);
        final Map<String, Boolean> checks = new HashMap<>();
        return Stream.of(CompilerType.values()).map(type -> type.newHtmlCompiler(log, archive, checks));
    }

    public static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

}
