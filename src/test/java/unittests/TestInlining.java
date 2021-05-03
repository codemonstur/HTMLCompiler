package unittests;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.api.Test;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newHtmlCompiler;

public class TestInlining {

    private static final String html_output_image =
        "<!DOCTYPE html><html><head><title>Test html</title></head><body><img src=\"data:image/jpeg;base64," +
        "/9j/4AAQSkZJRgABAQEASABIAAD//gATQ3JlYXRlZCB3aXRoIEdJTVD" +
        "/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT" +
        "/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT" +
        "/wgARCAABAAEDAREAAhEBAxEB/8QAFAABAAAAAAAAAAAAAAAAAAAACP/EABQBAQAAAAAAAAAAAAAAAAAAAAD" +
        "/2gAMAwEAAhADEAAAAVSf/8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABBQJ//8QAFBEBAAAAAAAAAAAAAAAAAAAAAP" +
        "/aAAgBAwEBPwF//8QAFBEBAAAAAAAAAAAAAAAAAAAAAP/aAAgBAgEBPwF//8QAFBABAAAAAAAAAAAAAAAAAAAAAP" +
        "/aAAgBAQAGPwJ//8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPyF//9oADAMBAAIAAwAAABCf" +
        "/8QAFBEBAAAAAAAAAAAAAAAAAAAAAP/aAAgBAwEBPxB//8QAFBEBAAAAAAAAAAAAAAAAAAAAAP/aAAgBAgEBPxB" +
        "//8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPxB//9k=\"></body></html>";

    @Test
    public void compileInlineImage() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/inlining/image.html");
        assertEquals("Invalid generated HTML", html_output_image, output);
    }

    public static final String html_output_favicon =
        "<!DOCTYPE html><html><head><link rel=\"icon\" type=\"image/ico\" href=\"data:image/ico;base64," +
        "PGh0bWw+CjxoZWFkPgogICAgPGxpbmsgcmVsPSJpY29uIiB0eXBlPSJpbWFnZS9pY28iIGhyZWY9Ii4uL3N0YXRpYy9pbW" +
        "cvZmF2aWNvbi5pY28iIGlubGluZT4KICAgIDx0aXRsZT5UZXN0IGh0bWw8L3RpdGxlPgo8L2hlYWQ+Cjxib2R5PgoKPC9i" +
        "b2R5Pgo8L2h0bWw+\"><title>Test html</title></head><body></body></html>";

    @Test
    public void compileInlineFavicon() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/inlining/favicon.html");
        assertEquals("Invalid generated HTML", html_output_favicon, output);
    }

    public static final String html_output_script =
        "<!DOCTYPE html><html><head><script>function merging1(){console.log(\"Merging 1\")};</script>" +
        "<title>Test html</title><script>\n" +
        "function merging1() {\n" +
        "    console.log(\"Merging 1\");\n" +
        "}\n" +
        "</script></head><body></body></html>";

    @Test
    public void compileInlineScript() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/inlining/script.html");
        assertEquals("Invalid generated HTML", html_output_script, output);
    }

    public static final String html_output_style =
        "<!DOCTYPE html><html><head><style>*{width:100%}</style><title>Test html</title><style>* {\n" +
        "    width: 100%;\n" +
        "}\n" +
        "</style></head><body></body></html>";

    @Test
    public void compileInlineStyle() throws IOException, InvalidInput {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = Parsing.compileFile(compiler, "src/test/resources/inlining/style.html");
        assertEquals("Invalid generated HTML", html_output_style, output);
    }

}
