package htmlcompiler.tags.jsoup;

import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.tools.IO;
import org.jsoup.nodes.Element;

import java.io.File;

import static htmlcompiler.model.ImageType.isBinaryImage;
import static htmlcompiler.tools.IO.toLocation;

public enum Image {;

    public static TagVisitor newImageVisitor(JsoupCompiler compiler) {
        return (TagVisitor.TailVisitor) (file, node, depth) -> {
            if (node.hasAttr("inline")) {
                final File location = toLocation(file, node.attr("src"), "img tag in %s has an invalid src location '%s'");

                if (location.getName().endsWith(".svg")) {
                    final Element newImage = compiler.compileHtmlFragment(location, IO.toString(location)).child(0);
                    node.removeAttr("inline");

                    TagParsingJsoup.copyAttributes(node, newImage);
                    TagParsingJsoup.replaceWith(node, newImage);

                } else if (isBinaryImage(location.getName())) {
                    node.removeAttr("inline");
                    node.attr("src", TagParsingJsoup.toDataUrl(location));
                }
            } else if (node.hasAttr("to-absolute")) {
                TagParsingJsoup.makeAbsolutePath(node, "src");
            }
        };
    }

}
