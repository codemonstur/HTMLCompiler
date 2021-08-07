package unittests;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.api.Test;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newHtmlCompiler;

public class TestCompression {

    private static final String html_output_compress_html =
        "<!DOCTYPE html><html><head><script type=\"text/html\"><li><a href=\"/tour/{{>id}}\">{{&gt;" +
        "title}}</a></li><b>Hello</b></script></head><body></body></html>";

    @Test
    public void compressHtml() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/html/compress-html.html");
        assertEquals(html_output_compress_html, output, "Invalid generated HTML");
    }

}
