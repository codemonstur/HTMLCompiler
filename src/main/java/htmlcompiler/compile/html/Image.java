package htmlcompiler.compile.html;

import org.w3c.dom.Element;

import java.io.File;

import static htmlcompiler.model.ImageType.isBinaryImage;
import static htmlcompiler.tools.HTML.*;
import static htmlcompiler.tools.IO.toLocation;

public enum Image {;

    public static TagProcessor newImageProcessor(final HtmlCompiler html) {
        return (inputDir, file, document, element) -> {
            if (element.hasAttribute("inline")) {
                final File location = toLocation(file, element.getAttribute("src"), "img tag in %s has an invalid src location '%s'");

                if (location.getName().endsWith(".svg")) {
                    final Element newImage = newElementOf(document, location, html);
                    element.removeAttribute("inline");
                    copyAttributes(element, newImage);
                    replaceTag(element, newImage);
                    return true;
                } else if (isBinaryImage(location.getName())) {
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
