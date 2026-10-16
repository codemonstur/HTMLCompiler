package htmlcompiler.pojos.library;

import java.util.List;

public record LibraryRecord(String name, String version, String type, String tagName, List<Attribute> attributes) {}
