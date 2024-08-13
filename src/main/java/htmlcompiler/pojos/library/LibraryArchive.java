package htmlcompiler.pojos.library;

import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static htmlcompiler.utils.Json.GSON;

public final class LibraryArchive {

    private final Map<LibraryKey, LibraryDescription> archive;

    public LibraryArchive() {
        this.archive = new HashMap<>();
        final var inLibArchive = LibraryArchive.class.getResourceAsStream("/htmlcompiler/library-archive.json");
        if (inLibArchive == null) throw new IllegalStateException("Missing /htmlcompiler/library-archive.json resource");
        try (final var reader = new InputStreamReader(inLibArchive)) {
            final List<LibraryRecord> list = GSON.fromJson(reader, new TypeToken<ArrayList<LibraryRecord>>(){}.getType());
            for (final LibraryRecord record : list) {
                archive.put(LibraryKey.toLibraryKey(record), LibraryDescription.toLibraryDescription(record));
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Parsing error during libary-archive load", e);
        }
    }

    public Element createTag(final Document document, final String name, final String version, final String type) {
        final LibraryDescription description = archive.get(new LibraryKey(name, version, type));
        final Element element = document.createElement(description.tagName);
        for (final Attribute attribute : description.attributes) {
            element.setAttribute(attribute.name, attribute.value);
        }
        return element;
    }

    public LibraryDescription get(final String name, final String version, final String type) {
        return archive.get(new LibraryKey(name, version, type));
    }
}
