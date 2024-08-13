package unittests;

import htmlcompiler.compilers.HtmlCompiler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newHtmlCompiler;
import static util.Parsing.compileFile;

public class TestTagMerging {

    private static final String output_html_five_scripts =
        "<!DOCTYPE html><html lang=\"en\"><head></head><body><script>'use strict';function merging1(){console.log(\"Merging " +
        "1\")};function merging2(){console.log(\"Merging 2\")};function merging3(){console.log(\"Merging " +
        "3\")};function merging4(){console.log(\"Merging 4\")};function merging5(){console.log(\"Merging " +
        "5\")};</script></body></html>";
    private static final String output_html_two_links =
        "<!DOCTYPE html><html lang=\"en\"><head><style>*{width:100%}div,p{color:#eee}" +
        ".classone{color:#fff}#idone{color:#000}body{margin:0}body .panel{border:0}body .panel,b " +
        ".panel{border:0}*{width:100%}div,p{color:#eee}" +
        ".classone{color:#fff}#idone{color:#000}body{margin:0}body .panel{border:0}body .panel,b " +
        ".panel{border:0}</style></head><body class=\"nav-sm footer_fixed\"></body></html>";

    @Test
    public void mergeFiveScriptTags() throws Exception {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = compileFile(compiler, "src/test/resources/merging/test3.html");
        assertEquals(output_html_five_scripts, output, "Merge output not equal");
    }

    @Test
    public void mergeTwoLinkTags() throws Exception {
        final HtmlCompiler compiler = newHtmlCompiler();
        final String output = compileFile(compiler, "src/test/resources/merging/test2.html");
        assertEquals(output_html_two_links, output, "Merge output not equal");
    }

}
