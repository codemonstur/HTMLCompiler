package unittests;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Parsing;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class TestIntegrity {

    private static final String html_output_integrity_default =
        "<!DOCTYPE html><html><head><title>Test html</title><script async defer src=\"https://apis.google" +
        ".com/js/platform.js\" integrity=\"sha384-KHNOakmEJYbkPLSAox48auZaFAt+E9IYHFf6rFHA+UtXhL" +
        "+/65qSTC8qPa2bKxZl\" crossorigin=\"anonymous\"></script><script src=\"https://www.google" +
        ".com/recaptcha/api.js\"></script></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileIntegrityDefault(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/integrity-default.html");
        assertEquals("Invalid generated HTML", html_output_integrity_default, output);
    }

    private static final String html_output_integrity_disable =
        "<!DOCTYPE html><html><head><title>Test html</title><script async defer src=\"https://apis.google" +
        ".com/js/platform.js\"></script></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileIntegrityDisable(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/integrity-disable.html");
        assertEquals("Invalid generated HTML", html_output_integrity_disable, output);
    }

    private static final String html_output_integrity_force =
        "<!DOCTYPE html><html><head><title>Test html</title><script src=\"https://www.google.com/recaptcha/api.js\" " +
        "integrity=\"sha384-kp3la1AzGc/SpDx/1HegMoGOSC9o9doWJLb3H20jZpwCfm5VnuMLcRcXl9BW5R9U\" " +
        "crossorigin=\"anonymous\"></script></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileIntegrityForce(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/integrity-force.html");
        assertEquals("Invalid generated HTML", html_output_integrity_force, output);
    }

}
