package unittests;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestScriptEncoding {

    private static final String html_output_script_encode =
        "<!DOCTYPE html><html><head><title>Test html</title><script>if(1>2){console.log(\"Hello, world!\")}if(1<2){console.log(\"Hello, world!\")};</script></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileScriptEncode(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/scripts.html");
        assertEquals("Invalid generated HTML", html_output_script_encode, output);
    }

}
