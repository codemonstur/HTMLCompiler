package unittests;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.api.Test;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newHtmlCompiler;

public class TestLibraryInsert {

    private static final String html_output_with_library =
        "<!DOCTYPE html><html><head><title>TestApp html</title><script crossorigin=\"anonymous\" " +
        "integrity=\"sha384-xBuQ/xzmlsLoJpyjoggmTEz8OWUFM0/RC5BsqQBDX2v5cMvDHcMakNTNrHIW2I5f\" " +
        "src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min" +
        ".js\"></script></head><body></body></html>";

    // This test fails with Jsoup because it doesn't recognise the <library> tag and moves it to the body
    // Don't have a solution for this right now. Fortunately the meta-tag option works fine
    @Test
    public void compileLibraryTag() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/library.html");
        assertEquals(html_output_with_library, output, "Invalid generated HTML");
    }

    @Test
    public void compileLibraryMeta() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/library-meta.html");
        assertEquals(html_output_with_library, output, "Invalid generated HTML");
    }

}
