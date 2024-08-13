package unittests;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.api.Test;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newHtmlCompiler;

public class TestIntegrity {

    private static final String
            html_output_integrity_default = "<!DOCTYPE html><html><head><title>TestApp html</title></head><body><script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js\" integrity=\"sha384-1H217gwSVyLSIfaLxHbE7dRb3v4mYCKbpQvzx0cegeju1MVsGrX5xXxAvs/HgeFs\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script></body></html>",
            html_output_integrity_disable = "<!DOCTYPE html><html><head><title>TestApp html</title><script async defer src=\"https://apis.google.com/js/platform.js\"></script></head><body></body></html>",
            html_output_integrity_force = "<!DOCTYPE html><html><head><title>TestApp html</title><script src=\"https://www.google.com/recaptcha/api.js\" integrity=\"sha384-ehbJYF277zAUkSr91XXb7CvUNHttAN7U2YlYHG1ojhA3oUCCoI2AFTy+ZaM2A5Le\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script></head><body></body></html>";

    @Test
    public void compileIntegrityDefault() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/integrity-default.html");
        assertEquals(html_output_integrity_default, output, "Invalid generated HTML");
    }

    @Test
    public void compileIntegrityDisable() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/integrity-disable.html");
        assertEquals(html_output_integrity_disable, output, "Invalid generated HTML");
    }

    @Test
    public void compileIntegrityForce() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/integrity-force.html");
        assertEquals(html_output_integrity_force, output, "Invalid generated HTML");
    }

}
