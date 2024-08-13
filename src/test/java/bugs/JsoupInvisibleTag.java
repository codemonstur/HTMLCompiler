package bugs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

public class JsoupInvisibleTag {

    private static final String html =
        "<html>\n" +
        "<head>\n" +
        "    <link href=\"css/test.css\" rel=\"stylesheet\">\n" +
        "    <link href=\"css/test.css\" rel=\"stylesheet\">\n" +
        "</head>\n" +
        "<body>\n" +
        "<script src=\"test1.js\"></script>\n" +
        "<script src=\"test2.js\"></script>\n" +
        "</body>\n" +
        "</html>";

    public static void main(final String... args) {
        Document document = Jsoup.parse(html);
        document.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {}
            public void tail(Node node, int depth) {
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if ("link".equals(element.tagName())) {
                        Element style = element.ownerDocument().createElement("style");
                        markForRemoval(element, style);
                    }
                    if ("script".equals(element.tagName()) && element.hasAttr("src")) {
                        Element script = element.ownerDocument().createElement("script");
                        markForRemoval(element, script);
                    }
                    if ("style".equals(element.tagName())) {
                        System.out.println("I found a style tag with sibling? " + previousSiblingElement(element));
                    }
                }
            }
        });
        document.select("*[htmlcompiler=delete-me]").remove();
        System.out.println(document.html());
    }

    private static Element previousSiblingElement(Element element) {
        Element previous = element.previousElementSibling();
        while (previous != null) {
            if (previous.hasAttr("htmlcompiler")) {
                previous.remove();
                previous = element.previousElementSibling();
            } else {
                break;
            }
        }

        return previous;
    }

    private static void markForRemoval(Element element, Element next) {
        element.attr("htmlcompiler", "delete-me");
        element.after(next);
    }

}
