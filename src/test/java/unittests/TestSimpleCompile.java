package unittests;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.error.InvalidInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Parsing;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class TestSimpleCompile {

    private static final String output_html_doctype =
        "<!DOCTYPE html><html lang=\"en\"><head><meta http-equiv=\"X-UA-Compatible\" " +
        "content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, " +
        "initial-scale=1\"><title>Test doctype</title></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileWithDoctype(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/with-doctype.html");
        assertEquals("Invalid generated HTML", output_html_doctype, output);
    }

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileWithoutDoctype(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/without-doctype.html");
        assertEquals("Invalid generated HTML", output_html_doctype, output);
    }

}
