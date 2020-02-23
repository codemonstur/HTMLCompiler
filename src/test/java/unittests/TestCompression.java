package unittests;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompression {

    private static final String html_output_compress_html =
        "<!DOCTYPE html><html><head><script type=\"text/html\"><li><a href=\"/tour/{{>id}}\">{{&gt;" +
        "title}}</a></li><b>Hello</b></script></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compressHtml(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/compress-html.html");
        assertEquals("Invalid generated HTML", html_output_compress_html, output);
    }

}
