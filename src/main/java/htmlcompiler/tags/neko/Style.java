package htmlcompiler.tags.neko;

import htmlcompiler.tools.IO;

import java.io.File;

import static htmlcompiler.compilers.css.CssCompiler.compressCssCode;
import static htmlcompiler.tags.neko.TagParsingNeko.isEmpty;
import static htmlcompiler.tags.neko.TagParsingNeko.makeAbsolutePath;
import static htmlcompiler.tools.IO.toLocation;

public enum Style {;

    public static TagProcessor newStyleProcessor() {
        return (file, document, element) -> {
            if (element.hasAttribute("inline")) {
                final File location = toLocation(file, element.getAttribute("src"), "style tag in %s has an invalid src location '%s'");

                element.removeAttribute("inline");
                element.removeAttribute("src");
                element.setTextContent(compressCssCode(IO.toString(location)));

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
                element.setTextContent(compressCssCode(element.getTextContent()));

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

}
