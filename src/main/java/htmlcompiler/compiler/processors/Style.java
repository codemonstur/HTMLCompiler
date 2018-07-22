package htmlcompiler.compiler.processors;

import htmlcompiler.compiler.CssCompiler;
import htmlcompiler.util.IO;

import java.io.File;

import static htmlcompiler.compiler.processors.TagProcessor.notEmpty;
import static htmlcompiler.util.HTML.makeAbsolutePath;

public enum Style {;

    public static TagProcessor newStyleProcessor(final CssCompiler css) {
        return (loader, file, document, element) -> {
            if (element.hasAttribute("inline")) {
                final File location = loader.toLocation(file, element.getAttribute("src"), "style tag in %s has an invalid src location '%s'");

                element.removeAttribute("inline");
                element.removeAttribute("src");
                element.setTextContent(css.compress(IO.toString(location)));
                return true;
            }
            if (notEmpty(element.getTextContent())) {
                element.setTextContent(css.compress(element.getTextContent()));
            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
            return false;
        };
    }
}
