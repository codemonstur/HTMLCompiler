package htmlcompiler;

import htmlcompiler.compile.TemplateThenCompile;
import htmlcompiler.model.Task;
import htmlcompiler.services.LoopingSingleThread;
import htmlcompiler.services.Service;
import htmlcompiler.tools.LogSuppressingMojo;
import htmlcompiler.tools.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static htmlcompiler.compile.MavenProjectReader.toInputDirectory;
import static htmlcompiler.compile.MavenProjectReader.toOutputDirectory;
import static htmlcompiler.compile.TemplateThenCompile.RenameFile.defaultRenamer;
import static htmlcompiler.services.DirectoryWatcher.newDirectoryWatcher;
import static htmlcompiler.services.Http.newHttpServer;
import static htmlcompiler.tools.IO.relativize;
import static htmlcompiler.tools.Logger.YYYY_MM_DD_HH_MM_SS;
import static htmlcompiler.tools.Logger.newLogger;
import static java.lang.String.format;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Mojo( name = "host" )
public final class MavenHost extends LogSuppressingMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Parameter(defaultValue = "8080")
    public int port;

    @Parameter(defaultValue = "true")
    public boolean replaceExtension;

    @Parameter()
    public String watchedDirectories;

    @Parameter(defaultValue = "true")
    public boolean requestApiEnabled;

    @Parameter(defaultValue = "src/main/websrc/requests.json")
    public String requestApiSpecification;

    @Override
    public void execute() throws MojoFailureException {
        final Logger log = newLogger(getLog()::info, getLog()::warn);
        final File inputDir = toInputDirectory(project);
        final File outputDir = toOutputDirectory(project);

        try {
            final var server = newHttpServer(project, port, requestApiEnabled, requestApiSpecification);
            final var ttc = new TemplateThenCompile(log, defaultRenamer(inputDir, outputDir, replaceExtension), project);
            final var queue = new LinkedBlockingQueue<Task>();
            final var compiler = newTaskCompiler(log, queue, ttc, toChildrenSet(inputDir));
            final var watcher = newDirectoryWatcher()
                .directory(inputDir.toPath())
                .directories(toPathList(watchedDirectories))
                .listener((event, path) -> queue.add(new Task(event, path)))
                .build();

            server.start();
            compiler.start();
            watcher.start();

            log.info("Listening on localhost:" + port);
            log.info(format
                ( "[%s] Compiling supported template formats in %s to %s"
                , LocalDateTime.now().format(YYYY_MM_DD_HH_MM_SS)
                , relativize(project.getBasedir(), inputDir)
                , relativize(project.getBasedir(), outputDir)
                ));
            watcher.waitUntilDone();
        } catch (IOException | InterruptedException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    private static Service newTaskCompiler(final Logger log, final BlockingQueue<Task> queue, final TemplateThenCompile ttc, final Set<Path> rootPages) {
        return new LoopingSingleThread(() -> {
            final Task take = queue.take();
            if (take.type == ENTRY_DELETE && take.path.endsWith("/.")) return;
            if (take.type == ENTRY_CREATE && take.path.toFile().isDirectory()) return;

            try {
                final boolean doAll = rootPages.contains(take.path.normalize().toAbsolutePath());
                if (doAll) {
                    queue.clear();
                    for (final var path :rootPages) {
                        ttc.compileTemplate(path.toFile());
                    }
                    return;
                }
                ttc.compileTemplate(take.path.toFile());
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });
    }

    private static List<Path> toPathList(final String semicolonSeparatedList) {
        if (semicolonSeparatedList == null || semicolonSeparatedList.isEmpty()) return emptyList();

        return Arrays.stream(semicolonSeparatedList.split(";"))
            .map(path -> Paths.get(path))
            .collect(toList());
    }

    private static Set<Path> toChildrenSet(final File inputDir) {
        final var children = requireNonNull(inputDir.listFiles());
        return Arrays.stream(children)
            .map(File::toPath)
            .collect(toSet());
    }

}
