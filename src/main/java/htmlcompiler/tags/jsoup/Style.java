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
                setData(element, compressCssCode(IO.toString(location)));

                final Element previousSibling = previousElementSibling(element);
                if (isInlineStyle(previousSibling) && !isEmpty(previousSibling)) {
                    setData(element, previousSibling.data() + element.data());
                    previousSibling.attr("htmlcompiler", "delete-me");
                }
                return;
            }

            if (!isEmpty(element)) {
                setData(element, compressCssCode(element.data()));

                final Element previousSibling = previousElementSibling(element);
                if (isInlineStyle(previousSibling) && !isEmpty(previousSibling)) {
                    setData(element, previousSibling.data() + element.data());
                    previousSibling.attr("htmlcompiler", "delete-me");
                }

                return;
            }
            if (element.hasAttr("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
        };
    }

}
