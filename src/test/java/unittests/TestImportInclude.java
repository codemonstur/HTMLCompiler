package unittests;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.error.InvalidInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Parsing;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class TestImportInclude {

    private static final String html_output_with_footer =
        "<!DOCTYPE html><html lang=\"en\"><head><title>Test import</title></head><body><footer><div " +
        "class=\"pull-right\"> Unit test </div><div class=\"clearfix\"></div></footer></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileImport(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/including/import.html");
        assertEquals("Invalid generated HTML", html_output_with_footer, output);
    }

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileInclude(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/including/include.html");
        assertEquals("Invalid generated HTML", html_output_with_footer, output);
    }

}
