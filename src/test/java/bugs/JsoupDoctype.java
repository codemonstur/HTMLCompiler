package bugs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsoupDoctype {

    private static final String html =
        "<!DOCTYPE html>\n" +
        "<html>\n" +
        "<head>\n" +
        "</head>\n" +
        "<body>\n" +
        "</body>\n" +
        "</html>";

    public static void main(final String... args) {
        final Document document = Jsoup.parse(html);
        assertEquals("Doctype is not upper case", "!DOCTYPE", document.childNode(0).nodeName());
    }

}
