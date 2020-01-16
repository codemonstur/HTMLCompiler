package htmlcompiler.pojos.compile;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public final class Task {
    public final WatchEvent.Kind type;
    public final Path path;

    public Task(final WatchEvent.Kind type, final Path path) {
        this.type = type;
        this.path = path;
    }
}