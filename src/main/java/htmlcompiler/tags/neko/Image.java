package htmlcompiler.tags.neko;

import htmlcompiler.compilers.html.NekoCompiler;
import org.w3c.dom.Element;

import java.nio.file.Path;

import static htmlcompiler.pojos.compile.ImageType.isBinaryImage;
import static htmlcompiler.tags.neko.TagParsingNeko.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Image {;

    public static TagProcessor newImageProcessor(final NekoCompiler html) {
        return (file, document, element) -> {
            if (element.hasAttribute("inline")) {
                final Path location = toLocation(file, element.getAttribute("src"), "img tag in %s has an invalid src location '%s'");

                if (location.toString().endsWith(".svg")) {
                    final Element newImage = newElementOf(document, location, html);
                    element.removeAttribute("inline");
                    element.removeAttribute("src");
                    copyAttributes(element, newImage);
                    replaceTag(element, newImage);
                    return true;
                } else if (isBinaryImage(location)) {
                    element.removeAttribute("inline");
                    element.setAttribute("src", toDataUrl(location));
                }
            } else if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
            return false;
        };
    }

}
