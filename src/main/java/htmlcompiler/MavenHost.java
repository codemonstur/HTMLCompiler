package htmlcompiler;

import com.google.gson.Gson;
import htmlcompiler.compilers.TemplateThenCompile;
import htmlcompiler.library.LibraryArchive;
import htmlcompiler.model.CompilerType;
import htmlcompiler.model.Task;
import htmlcompiler.services.LoopingSingleThread;
import htmlcompiler.services.Service;
import htmlcompiler.tools.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static htmlcompiler.MavenProjectReader.toInputDirectory;
import static htmlcompiler.MavenProjectReader.toOutputDirectory;
import static htmlcompiler.checks.ReadCheckConfiguration.readChecksConfiguration;
import static htmlcompiler.compilers.RenameFile.defaultRenamer;
import static htmlcompiler.compilers.TemplateThenCompile.newTemplateThenCompile;
import static htmlcompiler.services.DirectoryWatcher.newDirectoryWatcher;
import static htmlcompiler.services.Http.newHttpServer;
import static htmlcompiler.templates.TemplateEngine.newExtensionToEngineMap;
import static htmlcompiler.tools.App.buildMavenTask;
import static htmlcompiler.tools.IO.relativize;
import static htmlcompiler.tools.Logger.YYYY_MM_DD_HH_MM_SS;
import static java.lang.String.format;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Mojo( name = "host" )
public final class MavenHost extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Parameter(defaultValue = "8080")
    public int port;

    @Parameter(defaultValue = "true")
    public boolean replaceExtension;
    @Parameter(defaultValue = "src/main/webinc")
    public String watchedDirectories;
    @Parameter(defaultValue = "true")
    public boolean requestApiEnabled;
    @Parameter(defaultValue = "src/main/websrc/requests.json")
    public String requestApiSpecification;
    @Parameter(defaultValue = "src/main/websrc/validation.json")
    public String validation;
    @Parameter(defaultValue = "jsoup")
    public CompilerType type;

    @Override
    public void execute() throws MojoFailureException {
        buildMavenTask(this, log -> {
            final var inputDir = toInputDirectory(project);
            final var outputDir = toOutputDirectory(project);

            final var gson = new Gson();
            final var libs = new LibraryArchive(gson);
            final var templates = newExtensionToEngineMap(project);
            final var checksSettings = readChecksConfiguration(validation, gson);
            final var html = type.newHtmlCompiler(log, libs, checksSettings);
            final var ttc = newTemplateThenCompile(templates, defaultRenamer(inputDir, outputDir, replaceExtension), html);
            final var queue = new LinkedBlockingQueue<Task>();

            final var server = newHttpServer(project, port, requestApiEnabled, requestApiSpecification);
            final var compiler = newTaskCompiler(log, inputDir, queue, ttc, toChildrenSet(inputDir));
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
        });
    }

    private static Service newTaskCompiler(final Logger log, final File rootDir, final BlockingQueue<Task> queue
            , final TemplateThenCompile ttc, final Set<Path> rootPages) {
        return new LoopingSingleThread(() -> {
            final Task take = queue.take();
            if (take.type == ENTRY_DELETE && take.path.endsWith("/.")) return;
            if (take.type == ENTRY_CREATE && take.path.toFile().isDirectory()) return;

            try {
                final boolean isKnownTemplate = rootPages.contains(take.path.normalize().toAbsolutePath());
                if (isKnownTemplate) {
                    log.warn("Compiling file " + take.path);
                    ttc.compileTemplate(take.path.toFile());
                    log.warn("Compiled file " + take.path);
                } else {
                    if (isChildOf(take.path, rootDir))
                        rootPages.add(take.path.normalize().toAbsolutePath());

                    queue.clear();
                    log.warn("Compiling all files in root");
                    for (final var path : rootPages) {
                        log.warn("Compiling file " + take.path);
                        ttc.compileTemplate(path.toFile());
                        log.warn("Compiled file " + take.path);
                    }
                    log.warn("Compiled all files in root");
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static boolean isChildOf(final Path path, final File directory) {
        return directory.toPath().toAbsolutePath().startsWith(path.toAbsolutePath());
    }

    private static List<Path> toPathList(final String semicolonSeparatedList) {
        if (semicolonSeparatedList == null || semicolonSeparatedList.isEmpty()) return emptyList();

        return Arrays.stream(semicolonSeparatedList.split(";"))
            .map(path -> Paths.get(path))
            .collect(toList());
    }

    private static Set<Path> toChildrenSet(final File inputDir) {
        return Arrays.stream(requireNonNull(inputDir.listFiles()))
            .map(File::toPath)
            .map(Path::normalize)
            .map(Path::toAbsolutePath)
            .collect(toSet());
    }

}
