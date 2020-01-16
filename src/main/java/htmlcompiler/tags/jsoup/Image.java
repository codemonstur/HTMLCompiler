package htmlcompiler.tags.jsoup;

import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import htmlcompiler.tools.IO;
import org.jsoup.nodes.Element;

import java.io.File;

import static htmlcompiler.pojos.compile.ImageType.isBinaryImage;
import static htmlcompiler.tags.jsoup.TagParsingJsoup.*;
import static htmlcompiler.tags.jsoup.TagParsingJsoup.copyAttributes;
import static htmlcompiler.tags.jsoup.TagParsingJsoup.makeAbsolutePath;
import static htmlcompiler.tools.IO.toLocation;

public enum Image {;

    public static TagVisitor newImageVisitor(JsoupCompiler compiler) {
        return (TailVisitor) (file, node, depth) -> {
            if (node.hasAttr("inline")) {
                final File location = toLocation(file, node.attr("src"), "img tag in %s has an invalid src location '%s'");

                if (location.getName().endsWith(".svg")) {
                    final Element newImage = compiler.compileHtmlFragment(location, IO.toString(location)).child(0);
                    node.removeAttr("inline");

                    copyAttributes(node, newImage);
                    replaceWith(node, newImage);

                } else if (isBinaryImage(location.getName())) {
                    node.removeAttr("inline");
                    node.attr("src", toDataUrl(location));
                }
            } else if (node.hasAttr("to-absolute")) {
                makeAbsolutePath(node, "src");
            }
        };
    }

}
