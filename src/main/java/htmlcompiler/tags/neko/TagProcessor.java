package htmlcompiler.tags.neko;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;

public interface TagProcessor {
    TagProcessor NOOP = (file, document, element) -> false;

    boolean process(File file, Document document, Element element) throws Exception;

    static boolean isEmpty(final Node script) {
        final String code = script.getTextContent();
        return code == null || code.trim().isEmpty();
    }

    static boolean isHtml(final Element script) {
        return script.hasAttribute("type") &&
        (  script.getAttribute("type").equalsIgnoreCase("text/x-jquery-tmpl")
        || script.getAttribute("type").equalsIgnoreCase("text/html")
        );
    }
    static boolean isCss(final Element link) {
        return !link.hasAttribute("type") || link.getAttribute("type").equalsIgnoreCase("text/css");
    }
}
