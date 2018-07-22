package htmlcompiler.compiler.processors;

import htmlcompiler.compiler.HtmlCompiler;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.util.Loader;
import org.w3c.dom.Element;

import java.io.File;

import static htmlcompiler.util.HTML.*;

public enum Import {;

    public static TagProcessor newImportProcessor(final HtmlCompiler html) {
        return (loader, file, document, element) -> {
            final Element root = loadHtml(html, toSourceLocation(element, "src", file, loader));
            if (root == null) element.getParentNode().removeChild(element);
            else replaceTag(element, toElementOf(document, root));
            return true;
        };
    }

    private static File toSourceLocation(final Element element, final String attribute, final File file, final Loader loader) throws InvalidInput {
        if (!element.hasAttribute(attribute)) throw new InvalidInput(String.format("<import> is missing '%s' attribute", attribute));
        return loader.toLocation(file.getParentFile(), element.getAttribute(attribute), "<import> in %s has an invalid src location '%s'");
    }

}
