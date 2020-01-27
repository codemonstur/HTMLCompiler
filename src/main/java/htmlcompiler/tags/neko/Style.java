package htmlcompiler.tags.neko;

import htmlcompiler.pojos.compile.StyleType;
import org.w3c.dom.Element;

import java.io.IOException;
import java.nio.file.Path;

import static htmlcompiler.compilers.scripts.CssCompiler.compressCssCode;
import static htmlcompiler.pojos.compile.StyleType.css;
import static htmlcompiler.pojos.compile.StyleType.detectStyleType;
import static htmlcompiler.tags.neko.TagParsingNeko.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Style {;

    public static TagProcessor newStyleProcessor() {
        return (file, document, element) -> {
            if (element.hasAttribute("inline")) {
                final Path location = toLocation(file, element.getAttribute("src"), "style tag in %s has an invalid src location '%s'");

                final StyleType type = detectStyleType(element, css);
                element.setTextContent(compressIfRequested(element, type.compile(location)));
                removeAttributes(element, "inline", "compress", "src", "type");

/*
                This code is supposed to merge adjacent tags together. It does not work.

                final Node previousSibling = getPreviousTagSibling(element, null);
                if (isInlineStyle(previousSibling) && !isEmpty(previousSibling)) {
                    element.setTextContent(previousSibling.getTextContent() + element.getTextContent());
                    element.getParentNode().removeChild(previousSibling);
                }
*/

                return false;
            }
            if (!isEmpty(element)) {
                final StyleType type = detectStyleType(element, css);
                element.setTextContent(compressIfRequested(element, type.compile(element.getTextContent(), file)));
                removeAttributes(element,"compress", "type");

/*
                This code is supposed to merge adjacent tags together. It does not work.

                final Node previousSibling = getPreviousTagSibling(element, null);
                if (isInlineStyle(previousSibling) && !isEmpty(previousSibling)) {
                    element.setTextContent(previousSibling.getTextContent() + element.getTextContent());
                    element.getParentNode().removeChild(previousSibling);
                }
*/

            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
            return false;
        };
    }

    private static String compressIfRequested(final Element element, final String code) throws IOException {
        if (code == null || code.isEmpty()) return code;
        return element.hasAttribute("compress") ? compressCssCode(code) : code;
    }

}
