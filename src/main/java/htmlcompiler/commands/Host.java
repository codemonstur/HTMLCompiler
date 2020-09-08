package htmlcompiler.commands;

import htmlcompiler.compilers.TemplateThenCompile;
import htmlcompiler.pojos.compile.CompilerType;
import htmlcompiler.pojos.compile.Task;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.services.LoopingSingleThread;
import htmlcompiler.services.Service;
import htmlcompiler.tools.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static htmlcompiler.compilers.TemplateThenCompile.newTemplateThenCompile;
import static htmlcompiler.pojos.compile.ChecksConfig.readChecksConfiguration;
import static htmlcompiler.services.DirectoryWatcher.newDirectoryWatcher;
import static htmlcompiler.services.Http.newHttpServer;
import static htmlcompiler.tools.Logger.YYYY_MM_DD_HH_MM_SS;
import static java.lang.String.format;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public enum Host {;

    public static class HostCommandConfig {
        public CompilerType type;
        public String validation;
        public Path inputDir;
        public Path outputDir;
        public boolean replaceExtension;
        public Map<String, String> variables;
        public int port;
        public boolean requestApiEnabled;
        public String requestApiSpecification;
        public Path[] hostedPaths;
        public String watchedDirectories;
        public Path baseDir;
    }

    public static void executeHost(final Logger log, final HostCommandConfig config) throws IOException, InterruptedException {
        final var libs = new LibraryArchive();
        final var checksSettings = readChecksConfiguration(config.validation);
        final var html = config.type.newHtmlCompiler(log, libs, checksSettings);
        final var ttc = newTemplateThenCompile(config.inputDir, config.outputDir, config.replaceExtension, config.variables, html);
        final var queue = new LinkedBlockingQueue<Task>();

        final var server = newHttpServer(config.port, config.requestApiEnabled, config.requestApiSpecification, config.hostedPaths);
        final var compiler = newTaskCompiler(log, config.inputDir, queue, ttc, toChildrenSet(config.inputDir));
        final var watcher = newDirectoryWatcher()
                .directory(config.inputDir)
                .directories(toPathList(config.watchedDirectories))
                .listener((event, path) -> queue.add(new Task(event, path)))
                .build();

        server.start();
        compiler.start();
        watcher.start();

        log.info("Listening on localhost:" + config.port);
        log.info(format
            ( "[%s] Compiling supported template formats in %s to %s"
            , LocalDateTime.now().format(YYYY_MM_DD_HH_MM_SS)
            , config.baseDir.relativize(config.inputDir)
            , config.baseDir.relativize(config.outputDir)
            ));
        watcher.waitUntilDone();
    }

    private static Service newTaskCompiler(final Logger log, final Path rootDir, final BlockingQueue<Task> queue
            , final TemplateThenCompile ttc, final Set<Path> rootPages) {
        return new LoopingSingleThread(() -> {
            final Task take = queue.take();
            if (take.path == null) return;
            if (!isRegularFile(take.path)) return;
            if (take.type == ENTRY_DELETE) return;
            if (take.type == ENTRY_CREATE) return;

            try {
                final boolean isKnownTemplate = rootPages.contains(take.path.normalize().toAbsolutePath());
                if (isKnownTemplate) {
                    log.warn("Compiling file " + take.path);
                    ttc.compileTemplate(take.path);
                    log.warn("Compiled file " + take.path);
                } else {
                    if (isChildOf(take.path, rootDir))
                        rootPages.add(take.path.normalize().toAbsolutePath());

                    queue.clear();
                    log.warn("Compiling all files in root");
                    for (final var path : rootPages) {
                        log.warn("Compiling file " + path);
                        ttc.compileTemplate(path);
                        log.warn("Compiled file " + path);
                    }
                    log.warn("Compiled all files in root");
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static boolean isChildOf(final Path path, final Path directory) {
        return directory.toAbsolutePath().startsWith(path.toAbsolutePath());
    }

    private static List<Path> toPathList(final String semicolonSeparatedList) {
        if (semicolonSeparatedList == null || semicolonSeparatedList.isEmpty()) return emptyList();

        return Arrays.stream(semicolonSeparatedList.split(";"))
            .map(path -> Paths.get(path))
            .collect(toList());
    }

    private static Set<Path> toChildrenSet(final Path inputDir) throws IOException {
        return Files.list(inputDir)
            .map(Path::normalize)
            .map(Path::toAbsolutePath)
            .collect(toSet());
    }

}
