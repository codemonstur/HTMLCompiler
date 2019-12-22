package htmlcompiler.tags.jsoup;

import htmlcompiler.tools.IO;
import org.jsoup.nodes.Element;

import java.io.File;

import static htmlcompiler.compilers.css.CssCompiler.compressCssCode;
import static htmlcompiler.tools.IO.toLocation;

public enum Style {;

    public static TagVisitor newStyleVisitor() {
        return (TagVisitor.TailVisitor) (file, element, depth) -> {
            if (element.hasAttr("inline")) {
                final File location = toLocation(file, element.attr("src"), "style tag in %s has an invalid src location '%s'");

                TagParsingJsoup.removeAttributes(element, "inline", "src");
                element.text(compressCssCode(IO.toString(location)));

                final Element previousSibling = TagParsingJsoup.previousElementSibling(element);
                if (TagParsingJsoup.isInlineStyle(previousSibling) && !TagParsingJsoup.isEmpty(previousSibling)) {
                    element.text(previousSibling.text() + element.text());
                    previousSibling.remove();
                }
                return;
            }
            if (!TagParsingJsoup.isEmpty(element)) {
                element.text(compressCssCode(element.text()));

                final Element previousSibling = TagParsingJsoup.previousElementSibling(element);
                if (TagParsingJsoup.isInlineStyle(previousSibling) && !TagParsingJsoup.isEmpty(previousSibling)) {
                    element.text(previousSibling.text() + element.text());
                    previousSibling.remove();
                }

                return;
            }
            if (element.hasAttr("to-absolute")) {
                TagParsingJsoup.makeAbsolutePath(element, "src");
            }
        };
    }

}
