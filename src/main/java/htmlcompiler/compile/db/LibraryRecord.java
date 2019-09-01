package htmlcompiler.compile.db;

import java.util.List;

public final class LibraryRecord {
    public final String name;
    public final String version;
    public final String type;
    public final String tagName;
    public final List<Attribute> attributes;

    private LibraryRecord(final String name, final String version, final String type, final String tagName, final List<Attribute> attributes) {
        this.name = name;
        this.version = version;
        this.type = type;
        this.tagName = tagName;
        this.attributes = attributes;
    }
}