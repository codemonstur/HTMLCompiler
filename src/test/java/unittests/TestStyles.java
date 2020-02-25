package unittests;

import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Parsing;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStyles {

    private static final String html_output_less_external =
        "<!DOCTYPE html><html><head><title>Test html</title><style>#header{color:#6c94be}</style></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileExternalLess(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/styles/less_external.html");
        assertEquals("Invalid generated HTML", html_output_less_external, output);
    }

    private static final String html_output_less_inline =
            "<!DOCTYPE html><html><head><title>Test html</title><style>#header{color:#6c94be}</style></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileInlineLess(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/styles/less_inline.html");
        assertEquals("Invalid generated HTML", html_output_less_inline, output);
    }

    private static final String html_output_sass_external =
        "<!DOCTYPE html><html><head><title>Test html</title><style>h1{color:red;background-color:#00f;padding:.5em}</style></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileExternalSass(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/styles/sass_external.html");
        assertEquals("Invalid generated HTML", html_output_sass_external, output);
    }

    private static final String html_output_sass_inline =
        "<!DOCTYPE html><html><head><title>Test html</title><style>h1{color:red;background-color:#00f;padding:.5em}</style></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileInlineSass(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/styles/sass_inline.html");
        assertEquals("Invalid generated HTML", html_output_sass_inline, output);
    }
    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileInlineScss(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/styles/scss_inline.html");
        assertEquals("Invalid generated HTML", html_output_sass_inline, output);
    }

    private static final String html_output_stylus_external =
        "<!DOCTYPE html><html><head><title>Test html</title><style>body{font:14px/1.4 Helvetica,Arial,sans-serif}" +
        "a.button{-webkit-border-radius:5px;-moz-border-radius:5px;border-radius:5px}</style></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileExternalStylus(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/styles/stylus_external.html");
        assertEquals("Invalid generated HTML", html_output_stylus_external, output);
    }

    private static final String html_output_stylus_inline =
        "<!DOCTYPE html><html><head><title>Test html</title><style>body{font:14px/1.4 Helvetica,Arial,sans-serif}" +
        "a.button{-webkit-border-radius:5px;-moz-border-radius:5px;border-radius:5px}</style></head><body></body></html>";

    @ParameterizedTest
    @MethodSource("util.Factory#provideCompilers")
    public void compileInlineStylus(final HtmlCompiler compiler) throws IOException, InvalidInput {
        final String output = Parsing.compileFile(compiler, "src/test/resources/styles/stylus_inline.html");
        assertEquals("Invalid generated HTML", html_output_stylus_inline, output);
    }

}
