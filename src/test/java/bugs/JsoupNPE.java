package bugs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

public class JsoupNPE {

    private static final String html =
        "<html>\n" +
        "<head>\n" +
        "    <link href=\"css/test.css\" rel=\"stylesheet\">\n" +
        "</head>\n" +
        "<body>\n" +
        "</body>\n" +
        "</html>";
    public static void main(final String... args) {
        Document document = Jsoup.parse(html);
        document.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if ("link".equals(element.tagName())) {
                        Element style = element.ownerDocument().createElement("style");
                        element.replaceWith(style);
                    }
                }
            }

            @Override
            public void tail(Node node, int depth) {}
        });
    }

}
