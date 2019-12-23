package htmlcompiler.tags.jsoup;

import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import htmlcompiler.tools.IO;
import org.jsoup.nodes.Element;

import java.io.File;

import static htmlcompiler.compilers.css.CssCompiler.compressCssCode;
import static htmlcompiler.tags.jsoup.TagParsingJsoup.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Style {;

    public static TagVisitor newStyleVisitor() {
        return (TailVisitor) (file, element, depth) -> {
            if (element.hasAttr("inline")) {
                final File location = toLocation(file, element.attr("src"), "style tag in %s has an invalid src location '%s'");

                removeAttributes(element, "inline", "src");
                element.text(compressCssCode(IO.toString(location)));

                final Element previousSibling = previousElementSibling(element);
                if (isInlineStyle(previousSibling) && !isEmpty(previousSibling)) {
                    element.text(previousSibling.text() + element.text());
                    previousSibling.remove();
                }
                return;
            }
            if (!isEmpty(element)) {
                element.text(compressCssCode(element.text()));

                final Element previousSibling = previousElementSibling(element);
                if (isInlineStyle(previousSibling) && !isEmpty(previousSibling)) {
                    element.text(previousSibling.text() + element.text());
                    previousSibling.remove();
                }

                return;
            }
            if (element.hasAttr("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
        };
    }

}
