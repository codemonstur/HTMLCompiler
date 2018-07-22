package htmlcompiler.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.WatchEvent.Kind;

public enum Watcher {;

    public interface CheckedRunnable {
        void run() throws Exception;
    }

    public static Thread infiniteThread(final CheckedRunnable runnable) {
        return new Thread(() -> {
            while (true) {
                try { runnable.run(); }
                catch (Exception e) {}
            }
        });
    }

    public interface FileEventListener {
        void entryEvent(Kind type, Path entry) throws Exception;
    }

    public static void watchDirectory(final List<FileEventListener> listeners, final List<File> dirs) throws IOException {
        final WatchService watcher = FileSystems.getDefault().newWatchService();
        final Map<WatchKey, Path> keys = new HashMap<>();

        listeners.add((type, entry) -> {
            if (type != ENTRY_CREATE) return;
            if (!Files.isDirectory(entry)) return;
            walkAndRegisterDirectories(entry, watcher, keys);
        });

        for (final File dir : dirs) {
            walkAndRegisterDirectories(dir.toPath(), watcher, keys);
        }
        processEvents(watcher, keys, listeners);
    }

    private static void walkAndRegisterDirectories(final Path start, final WatchService watcher, final Map<WatchKey, Path> keys) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                keys.put(dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY), dir);
                return CONTINUE;
            }
        });
    }

    private static void processEvents(final WatchService watcher, final Map<WatchKey, Path> keys, final List<FileEventListener> listeners) {
        while (true) {
            final WatchKey key = take(watcher);
            if (key == null) continue;

            final Path dir = keys.get(key);
            if (dir == null) continue;

            for (final WatchEvent<?> event : key.pollEvents()) {
                final Kind kind = event.kind();
                final Path child = toPath(dir, event);

                for (final FileEventListener l : listeners) {
                    try {
                        l.entryEvent(kind, child);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (key.reset()) continue;

            keys.remove(key);
            if (keys.isEmpty()) break;
        }
    }

    private static WatchKey take(final WatchService watcher) {
        try {
            return watcher.take();
        } catch (InterruptedException x) {
            return null;
        }
    }

    private static Path toPath(final Path dir, final WatchEvent<?> event) {
        return dir.resolve(((WatchEvent<Path>)event).context());
    }
}
