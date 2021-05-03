package unittests;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.api.Test;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newHtmlCompiler;

public class TestImportInclude {

    private static final String html_output_with_footer =
        "<!DOCTYPE html><html lang=\"en\"><head><title>Test import</title></head><body><footer><div " +
        "class=\"pull-right\"> Unit test </div><div class=\"clearfix\"></div></footer></body></html>";

    @Test
    public void compileImport() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/including/import.html");
        assertEquals("Invalid generated HTML", html_output_with_footer, output);
    }

    @Test
    public void compileInclude() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/including/include.html");
        assertEquals("Invalid generated HTML", html_output_with_footer, output);
    }

}
