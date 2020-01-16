package htmlcompiler.pojos.library;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LibraryArchive {

    private final Map<LibraryKey, LibraryDescription> archive;
    public LibraryArchive(final Gson gson) {
        this.archive = new HashMap<>();
        try (final Reader reader = new InputStreamReader(LibraryArchive.class.getResourceAsStream("/library-archive.json"))) {
            final List<LibraryRecord> list = gson.fromJson(reader, new TypeToken<ArrayList<LibraryRecord>>(){}.getType());
            for (final LibraryRecord record : list) {
                archive.put(LibraryKey.toLibraryKey(record), LibraryDescription.toLibraryDescription(record));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Missing /library-archive.json resource");
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
