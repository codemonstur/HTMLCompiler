package htmlcompiler.compilers.tags;

import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.library.Attribute;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.pojos.library.LibraryDescription;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static htmlcompiler.compilers.tags.TagParsing.replaceWith;
import static xmlparser.utils.Functions.isNullOrEmpty;

public enum Meta {;

    public static TagVisitor newMetaVisitor(final LibraryArchive archive) {
        return (TagVisitor.TailVisitor) (config, file, node, depth) -> {
            final String name = node.attr("name");
            if ("library".equals(name)) {
                TagParsing.replaceWith(node, createTag(node, archive));
            }
        };
    }

    public static Element createTag(final Element element, final LibraryArchive library) throws InvalidInput {
        final String name = element.attr("content");
        final String version = element.attr("version");
        final String type = element.attr("type");
        if (isNullOrEmpty(name) || isNullOrEmpty(version) || isNullOrEmpty(type)) {
            throw new InvalidInput(String.format("Invalid library tag: name=%s, version=%s, type=%s", name, version, type));
        }

        return createTag(library.get(name, version, type), element.ownerDocument());
    }

    private static Element createTag(final LibraryDescription description, final Document document) {
        final Element newElement = document.createElement(description.tagName);
        for (final Attribute attribute : description.attributes) {
            newElement.attr(attribute.name, attribute.value);
        }
        return newElement;
    }

}
