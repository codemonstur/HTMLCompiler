package bugs;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newDefaultHtmlCompressor;

public class JsoupTagsInHead {

    // Jsoup has two types of parsers; the regular HTML parser, and the XML
    // parser. Neither parser can be used for HtmlCompiler for the following
    // reason:
    //
    // - When using the normal HTML parser an unknown tag in the head will be
    //   moved to the body.
    // - When using the XML parser auto-closing tags  will be given a closing tag

    private static final String
        html_with_unknown = "<html><head><unknown href=\"css/test.css\" rel=\"stylesheet\"/></head><body></body></html>",
        html_with_meta = "<html><head><meta name=\"generator\"></head><body></body></html>";

    // The regular parser will fail on unknown tags by moving them to the body, which prevents us from
    // using them as markers
    @Disabled @Test
    public void unknownTagInHead() {
        final var compressor = newDefaultHtmlCompressor();
        final var document1 = Jsoup.parse(html_with_unknown, "", Parser.xmlParser());
        final var document2 = Jsoup.parse(html_with_unknown);

        assertEquals(compressor.compress(html_with_unknown), compressor.compress(document1.html()), "Generated html differs");
        assertEquals(compressor.compress(html_with_unknown), compressor.compress(document2.html()), "Generated html differs");
    }

    // The XML parser will fail to generate correct HTML, because HTML isn't XML
    @Disabled @Test
    public void metaTagInHead() {
        final var compressor = newDefaultHtmlCompressor();
        final var document1 = Jsoup.parse(html_with_meta, "", Parser.xmlParser());
        final var document2 = Jsoup.parse(html_with_meta);

        assertEquals(compressor.compress(html_with_meta), compressor.compress(document1.html()), "Generated html differs");
        assertEquals(compressor.compress(html_with_meta), compressor.compress(document2.html()), "Generated html differs");
    }

}
