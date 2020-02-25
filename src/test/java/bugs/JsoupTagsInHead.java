package bugs;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Factory.newDefaultHtmlCompressor;

public class JsoupTagsInHead {

    // When there is an unknown tag in the head Jsoup moves it to the body
    // You can use the XmlParser to avoid this:
    //
    //   Jsoup.parse(content, "", Parser.xmlParser())
    //
    // Unfortunately this will prevent proper generation of HTML when you
    // call .html() on the document. The XML parser doesn't understand auto
    // closing tags.

    private static final String html_with_unknown =
        "<html><head><unknown href=\"css/test.css\" rel=\"stylesheet\" /></head><body></body></html>";

    private static final String html_with_meta =
        "<html>\n" +
        " <head>\n" +
        "  <meta name=\"generator\">\n" +
        " </head>\n" +
        " <body>\n" +
        " </body>\n" +
        "</html>";

    @Test
    public void unknownTagInHead() {
        final HtmlCompressor compressor = newDefaultHtmlCompressor();
        final Document document1 = Jsoup.parse(html_with_unknown, "", Parser.xmlParser());
        assertEquals("Generated html differs"
                , compressor.compress(html_with_unknown)
                , compressor.compress(document1.html()));

        final Document document2 = Jsoup.parse(html_with_unknown);
        assertEquals("Generated html differs"
                , compressor.compress(html_with_unknown)
                , compressor.compress(document2.html()));
    }

    @Test
    public void metaTagInHead() {
        final HtmlCompressor compressor = newDefaultHtmlCompressor();
        final Document document1 = Jsoup.parse(html_with_meta);
        assertEquals("Generated html differs"
                , compressor.compress(html_with_meta)
                , compressor.compress(document1.html()));

        final Document document2 = Jsoup.parse(html_with_meta, "", Parser.xmlParser());
        assertEquals("Generated html differs"
                , compressor.compress(html_with_meta)
                , compressor.compress(document2.html()));
    }

}
