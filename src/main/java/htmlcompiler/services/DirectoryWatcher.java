package htmlcompiler.services;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

public final class DirectoryWatcher {

    public interface PathEventListener {
        void onEvent(WatchEvent.Kind<Path> event, Path path);
    }

    private boolean emitCreatedForExisting = false;
    private PathEventListener listener;
    private Set<Path> directories = new HashSet<>();

    public static DirectoryWatcher newDirectoryWatcher() {
        return new DirectoryWatcher();
    }
    public DirectoryWatcher listener(final PathEventListener listener) {
        this.listener = listener;
        return this;
    }
    public DirectoryWatcher directories(final Collection<Path> directories) {
        for (final Path path : directories)
            this.directories.add(path.normalize());
        return this;
    }
    public DirectoryWatcher directory(final Path directory) {
        this.directories.add(directory.normalize());
        return this;
    }
    public DirectoryWatcher emitCreatedForExisting() {
        return emitCreatedForExisting(true);
    }
    public DirectoryWatcher emitCreatedForExisting(final boolean enabled) {
        this.emitCreatedForExisting = enabled;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Service build() throws IOException {
        if (directories.isEmpty()) throw new IllegalArgumentException("You must set at least 1 directory");
        if (listener == null) throw new IllegalArgumentException("You must set a listener");

        final var watchService = FileSystems.getDefault().newWatchService();
        final var watchKeyToDirectory = new HashMap<WatchKey, Path>();

        for (final var directory : directories) {
            try (final var walk = Files.walk(directory)) {
                walk.filter(path -> path.toFile().isDirectory())
                    .map(path -> notifyCreateEvent(emitCreatedForExisting, path, listener))
                    .forEach(path -> watchDirectory(path, watchService, watchKeyToDirectory));
            }
        }

        return new LoopingSingleThread(() -> {
            final var key = watchService.take();
            final var dir = watchKeyToDirectory.get(key);
            if (dir == null) return;

            for (final var event : key.pollEvents()) {
                if (event.kind().equals(OVERFLOW)) break;

                final var pathEvent = (WatchEvent<Path>) event;
                final var entryPath = dir.resolve(pathEvent.context());
                if (pathEvent.kind() == ENTRY_CREATE && entryPath.toFile().isDirectory())
                    watchDirectory(entryPath, watchService, watchKeyToDirectory);
                listener.onEvent(pathEvent.kind(), entryPath);
            }

            if (!key.reset()) {
                watchKeyToDirectory.remove(key);
                if (watchKeyToDirectory.isEmpty()) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private static void watchDirectory(final Path dir, final WatchService watchService, final Map<WatchKey, Path> watchKeyToDirectory) {
        try {
            watchKeyToDirectory.put(dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE), dir);
        } catch (IOException ioe) {
            System.err.println("Not watching '"+dir+"'.");
        }
    }

    private static Path notifyCreateEvent(final boolean enabled, final Path dir, final PathEventListener listener) {
        if (enabled) listener.onEvent(ENTRY_CREATE, dir);
        return dir;
    }
}
