package htmlcompiler.tags.neko;

import htmlcompiler.compilers.html.NekoCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import org.w3c.dom.Element;

import java.nio.file.Path;

import static htmlcompiler.tags.neko.TagParsingNeko.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Include {;

    public static TagProcessor newIncludeProcessor(final NekoCompiler html) {
        return (file, document, element) -> {
            final Element root = loadHtml(html, toSourceLocation(element, "src", file));
            if (root == null) deleteTag(element);
            else replaceTag(element, toElementOf(document, root));
            return true;
        };
    }

    private static Path toSourceLocation(final Element element, final String attribute, final Path file) throws InvalidInput {
        if (!element.hasAttribute(attribute)) throw new InvalidInput(String.format("<include> is missing '%s' attribute", attribute));
        return toLocation(file, element.getAttribute(attribute), "<include> in %s has an invalid src location '%s'");
    }

}
