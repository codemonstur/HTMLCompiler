package htmlcompiler.model.extjs;

import java.nio.file.Path;

public final class ImportNode implements Node {
    public final Integer lineNum;
    public final Path source;
    public final Path path;

    public ImportNode(final Integer lineNum, final Path source, final Path path) {
        this.lineNum = lineNum;
        this.source = source;
        this.path = path;
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
