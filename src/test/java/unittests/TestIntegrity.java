package unittests;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.api.Test;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newHtmlCompiler;

public class TestIntegrity {

    private static final String html_output_integrity_default =
        "<!DOCTYPE html><html><head><title>TestApp html</title><script async defer src=\"https://apis.google" +
        ".com/js/platform.js\" integrity=\"sha384-KHNOakmEJYbkPLSAox48auZaFAt+E9IYHFf6rFHA+UtXhL" +
        "+/65qSTC8qPa2bKxZl\" crossorigin=\"anonymous\"></script><script src=\"https://www.google" +
        ".com/recaptcha/api.js\"></script></head><body></body></html>";

    @Test
    public void compileIntegrityDefault() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/integrity-default.html");
        assertEquals(html_output_integrity_default, output, "Invalid generated HTML");
    }

    private static final String html_output_integrity_disable =
        "<!DOCTYPE html><html><head><title>TestApp html</title><script async defer src=\"https://apis.google" +
        ".com/js/platform.js\"></script></head><body></body></html>";

    @Test
    public void compileIntegrityDisable() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/integrity-disable.html");
        assertEquals(html_output_integrity_disable, output, "Invalid generated HTML");
    }

    private static final String html_output_integrity_force =
        "<!DOCTYPE html><html><head><title>TestApp html</title><script src=\"https://www.google.com/recaptcha/api.js\" " +
        "integrity=\"sha384-kp3la1AzGc/SpDx/1HegMoGOSC9o9doWJLb3H20jZpwCfm5VnuMLcRcXl9BW5R9U\" " +
        "crossorigin=\"anonymous\"></script></head><body></body></html>";

    @Test
    public void compileIntegrityForce() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/integrity-force.html");
        assertEquals(html_output_integrity_force, output, "Invalid generated HTML");
    }

}
