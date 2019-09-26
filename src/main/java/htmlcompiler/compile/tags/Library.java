package htmlcompiler.compile.tags;

import com.google.gson.Gson;
import htmlcompiler.compile.db.LibraryArchive;
import htmlcompiler.error.InvalidInput;

import java.io.IOException;

import static htmlcompiler.tools.HTML.replaceTag;
import static simplexml.utils.Functions.isNullOrEmpty;

public enum Library {;

    public static TagProcessor newLibraryProcessor(final Gson gson) throws IOException {
        final LibraryArchive archive = new LibraryArchive(gson);
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
