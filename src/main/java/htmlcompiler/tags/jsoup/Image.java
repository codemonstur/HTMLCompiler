package htmlcompiler.tags.jsoup;

import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import org.jsoup.nodes.Element;

import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.pojos.compile.ImageType.isBinaryImage;
import static htmlcompiler.tags.jsoup.TagParsingJsoup.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Image {;

    public static TagVisitor newImageVisitor(final JsoupCompiler compiler) {
        return (TailVisitor) (file, node, depth) -> {
            if (node.hasAttr("inline")) {
                final Path location = toLocation(file, node.attr("src"), "img tag in %s has an invalid src location '%s'");

                if (location.toString().endsWith(".svg")) {
                    final Element newImage = compiler.compileHtmlFragment(location, Files.readString(location)).child(0);
                    node.removeAttr("inline");
                    node.removeAttr("src");

                    copyAttributes(node, newImage);
                    replaceWith(node, newImage);

                } else if (isBinaryImage(location)) {
                    node.removeAttr("inline");
                    node.attr("src", toDataUrl(location));
                }
            } else if (node.hasAttr("to-absolute")) {
                makeAbsolutePath(node, "src");
            }
        };
    }

}
