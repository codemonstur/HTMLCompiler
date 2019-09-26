package htmlcompiler.compile.tags;

import htmlcompiler.tools.IO;

import java.io.File;

import static htmlcompiler.compile.css.CssCompiler.compressCssCode;
import static htmlcompiler.compile.tags.TagProcessor.isEmpty;
import static htmlcompiler.tools.HTML.makeAbsolutePath;
import static htmlcompiler.tools.IO.toLocation;

public enum Style {;

    public static TagProcessor newStyleProcessor() {
        return (file, document, element) -> {
            if (element.hasAttribute("inline")) {
                final File location = toLocation(file, element.getAttribute("src"), "style tag in %s has an invalid src location '%s'");

                element.removeAttribute("inline");
                element.removeAttribute("src");
                element.setTextContent(compressCssCode(IO.toString(location)));
                return true;
            }
            if (!isEmpty(element)) {
                element.setTextContent(compressCssCode(element.getTextContent()));
            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
            return false;
        };
    }
}
