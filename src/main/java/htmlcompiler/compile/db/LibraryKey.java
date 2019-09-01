package htmlcompiler.compile.db;

import java.util.Objects;

public final class LibraryKey {
    public final String name;
    public final String version;
    public final String type;

    public LibraryKey(final String name, final String version, final String type) {
        this.name = name;
        this.version = version;
        this.type = type;
    }

    public static LibraryKey toLibraryKey(final LibraryRecord record) {
        return new LibraryKey(record.name, record.version, record.type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryKey that = (LibraryKey) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(version, that.version) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, type);
    }
}
