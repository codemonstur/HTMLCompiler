package htmlcompiler.tags.neko;

import htmlcompiler.error.InvalidInput;
import htmlcompiler.library.LibraryArchive;

import static htmlcompiler.tags.neko.TagParsingNeko.replaceTag;
import static simplexml.utils.Functions.isNullOrEmpty;

public enum Meta {;

    public static TagProcessor newMetaProcessor(final LibraryArchive archive) {
        return (file, document, element) -> {
            final String meta = element.getAttribute("name");
            if (!"library".equals(meta)) return false;

            final String name = element.getAttribute("content");
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
