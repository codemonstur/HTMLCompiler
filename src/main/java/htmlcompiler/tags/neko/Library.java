package htmlcompiler.tags.neko;

import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.library.LibraryArchive;

import static htmlcompiler.tags.neko.TagParsingNeko.replaceTag;
import static simplexml.utils.Functions.isNullOrEmpty;

public enum Library {;

    public static TagProcessor newLibraryProcessor(final LibraryArchive archive) {
        return (file, document, element) -> {
            final String name = element.getAttribute("name");
            final String version = element.getAttribute("version");
            final String type = element.getAttribute("type");
            if (isNullOrEmpty(name) || isNullOrEmpty(version) || isNullOrEmpty(type)) {
                throw new InvalidInput(String.format("Invalid library tag: name=%s, version=%s, type=%s", name, version, type));
            }

            replaceTag(element, archive.createTag(document, name, version, type));
            return true;
        };
    }

}
