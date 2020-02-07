package util;

import com.google.gson.Gson;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.pojos.compile.ChecksConfig;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tools.Logger;

import java.util.HashMap;
import java.util.stream.Stream;

import static htmlcompiler.pojos.compile.CompilerType.nop;
import static htmlcompiler.tools.Logger.newLogger;
import static java.util.Collections.emptySet;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;

public enum Factory {;

    public static Stream<HtmlCompiler> provideCompilers() {
        final Gson gson = new Gson();
        final Logger log = newLogger(System.out::println, System.out::println);
        final LibraryArchive archive = new LibraryArchive(gson);
        final ChecksConfig checks = new ChecksConfig(emptySet(), emptySet(), new HashMap<>());
        return complementOf(of(nop)).stream().map(type -> type.newHtmlCompiler(log, archive, checks));
    }

    public static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

}
