package unittests;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.error.InvalidInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Parsing;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class TestLibraryInsert {

    private static final String html_output_with_library =
        "<!DOCTYPE html><html><head><title>Test html</title><script crossorigin=\"anonymous\" " +
        "integrity=\"sha384-xBuQ/xzmlsLoJpyjoggmTEz8OWUFM0/RC5BsqQBDX2v5cMvDHcMakNTNrHIW2I5f\" " +
        "src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min" +
        ".js\"></script></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileLibraryTag(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/library.html");
        assertEquals("Invalid generated HTML", html_output_with_library, output);
    }

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileLibraryMeta(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/library-meta.html");
        assertEquals("Invalid generated HTML", html_output_with_library, output);
    }

}
