package unittests;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.api.Test;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newHtmlCompiler;

public class TestScriptEncoding {

    private static final String html_output_script_encode =
        "<!DOCTYPE html><html><head><title>Test html</title><script>if(1>2){console.log(\"Hello, world!\")}if(1<2){console.log(\"Hello, world!\")};</script></head><body></body></html>";

    @Test
    public void compileScriptEncode() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/scripts.html");
        assertEquals("Invalid generated HTML", html_output_script_encode, output);
    }

}
