package htmlcompiler.pojos.library;

import java.util.List;

public final class LibraryDescription {
    public final String tagName;
    public final List<Attribute> attributes;

    private LibraryDescription(final String tagName, final List<Attribute> attributes) {
        this.tagName = tagName;
        this.attributes = attributes;
    }

    static LibraryDescription toLibraryDescription(final LibraryRecord record) {
        return new LibraryDescription(record.tagName, record.attributes);
    }
}
