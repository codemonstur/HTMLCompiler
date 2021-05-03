package unittests;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.api.Test;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newHtmlCompiler;

public class TestSimpleCompile {

    private static final String output_html_doctype =
        "<!DOCTYPE html><html lang=\"en\"><head><meta http-equiv=\"X-UA-Compatible\" " +
        "content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, " +
        "initial-scale=1\"><title>Test doctype</title></head><body></body></html>";

    @Test
    public void compileWithDoctype() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/with-doctype.html");
        assertEquals("Invalid generated HTML", output_html_doctype, output);
    }

    @Test
    public void compileWithoutDoctype() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/without-doctype.html");
        assertEquals("Invalid generated HTML", output_html_doctype, output);
    }

    private static final String html_output_uppercase =
        "<!DOCTYPE html><html><head><title>Test html</title></head><body></body></html>";

    @Test
    public void compileUppercase() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/uppercase.html");
        assertEquals("Invalid generated HTML", html_output_uppercase, output);
    }

    private static final String html_output_empty_script =
        "<!DOCTYPE html><html><head><title>Test html</title></head><body></body></html>";

    @Test
    public void compileEmptyScript() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/empty-script.html");
        assertEquals("Invalid generated HTML", html_output_empty_script, output);
    }

}
