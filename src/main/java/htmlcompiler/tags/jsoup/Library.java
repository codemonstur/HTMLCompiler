package htmlcompiler.tags.jsoup;

import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.library.Attribute;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.pojos.library.LibraryDescription;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import org.jsoup.nodes.Element;

import static htmlcompiler.tags.jsoup.TagParsingJsoup.replaceWith;
import static xmlparser.utils.Functions.isNullOrEmpty;

public enum Library {;

    public static TagVisitor newLibraryVisitor(final LibraryArchive archive) {
        return (TailVisitor) (config, file, node, depth) -> {
            replaceWith(node, createTag(node, archive));
        };
    }

    public static Element createTag(final Element element, final LibraryArchive library) throws InvalidInput {
        final String name = element.attr("name");
        final String version = element.attr("version");
        final String type = element.attr("type");
        if (isNullOrEmpty(name) || isNullOrEmpty(version) || isNullOrEmpty(type)) {
            throw new InvalidInput(String.format("Invalid library tag: name=%s, version=%s, type=%s", name, version, type));
        }

        final LibraryDescription description = library.get(name, version, type);
        final Element newElement = element.ownerDocument().createElement(description.tagName);
        for (final Attribute attribute : description.attributes) {
            newElement.attr(attribute.name, attribute.value);
        }
        return newElement;
    }

}
