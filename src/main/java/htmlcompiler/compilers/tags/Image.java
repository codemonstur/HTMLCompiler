package htmlcompiler.compilers.tags;

import htmlcompiler.compilers.HtmlCompiler;
import org.jsoup.nodes.Element;

import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.pojos.compile.ImageType.isBinaryImage;
import static htmlcompiler.tools.IO.toLocation;

public enum Image {;

    public static TagVisitor newImageVisitor(final HtmlCompiler compiler) {
        return (TagVisitor.TailVisitor) (config, file, node, depth) -> {
            if (node.hasAttr("inline")) {
                final Path location = toLocation(file, node.attr("src"), "img tag in %s has an invalid src location '%s'");

                if (location.toString().endsWith(".svg")) {
                    final Element newImage = compiler.compileHtmlFragment(location, Files.readString(location)).child(0);
                    node.removeAttr("inline");
                    node.removeAttr("src");

                    TagParsing.copyAttributes(node, newImage);
                    TagParsing.replaceWith(node, newImage);

                } else if (isBinaryImage(location)) {
                    node.removeAttr("inline");
                    node.attr("src", TagParsing.toDataUrl(location));
                }
            } else if (node.hasAttr("to-absolute")) {
                TagParsing.makeAbsolutePath(node, "src");
            }
        };
    }

}
