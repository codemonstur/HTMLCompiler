package htmlcompiler.tags.neko;

import htmlcompiler.tools.IO;
import org.w3c.dom.Node;

import java.io.File;

import static htmlcompiler.compilers.css.CssCompiler.compressCssCode;
import static htmlcompiler.tags.neko.TagParsingNeko.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Style {;

    public static TagProcessor newStyleProcessor() {
        return (file, document, element) -> {
            if (element.hasAttribute("inline")) {
                final File location = toLocation(file, element.getAttribute("src"), "style tag in %s has an invalid src location '%s'");

                element.removeAttribute("inline");
                element.removeAttribute("src");
                element.setTextContent(compressCssCode(IO.toString(location)));

                final Node previousSibling = getPreviousTagSibling(element, null);
                if (isInlineStyle(previousSibling) && !TagProcessor.isEmpty(previousSibling)) {
                    element.setTextContent(previousSibling.getTextContent() + element.getTextContent());
                    element.getParentNode().removeChild(previousSibling);
                }

                return false;
            }
            if (!TagProcessor.isEmpty(element)) {
                element.setTextContent(compressCssCode(element.getTextContent()));

                final Node previousSibling = getPreviousTagSibling(element, null);
                if (isInlineStyle(previousSibling) && !TagProcessor.isEmpty(previousSibling)) {
                    element.setTextContent(previousSibling.getTextContent() + element.getTextContent());
                    element.getParentNode().removeChild(previousSibling);
                }

            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
            return false;
        };
    }

}
