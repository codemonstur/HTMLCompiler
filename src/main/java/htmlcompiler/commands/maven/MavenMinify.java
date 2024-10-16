package htmlcompiler.commands.maven;

import htmlcompiler.minify.CssMinifyEngine;
import htmlcompiler.minify.HtmlMinifyEngine;
import htmlcompiler.minify.JsMinifyEngine;
import htmlcompiler.minify.Minifier;
import htmlcompiler.utils.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Consumer;

import static htmlcompiler.utils.App.buildMavenTask;
import static java.nio.file.Files.*;
import static java.nio.file.Files.isDirectory;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PREPARE_PACKAGE;

@Mojo(defaultPhase = PREPARE_PACKAGE, name = "minify")
public final class MavenMinify extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Parameter(defaultValue = "true")
    public boolean enabled;
    @Parameter
    public String targetDir;

    @Parameter(defaultValue = "hazendaz")
    public HtmlMinifyEngine htmlMinifyEngine;
    @Parameter(defaultValue = "yui")
    public CssMinifyEngine cssMinifyEngine;
    @Parameter(defaultValue = "gcc_simple")
    public JsMinifyEngine jsMinifyEngine;

    @Parameter
    public Set<String> files;
    @Parameter
    public Set<String> directories;

    public void execute() throws MojoFailureException {
        if (!enabled) return;

        buildMavenTask(this, log -> {
            log.info("HtmlCompiler minify command starting");

            final var htmlMinifier = htmlMinifyEngine.toMinifier();
            final var cssMinifier = cssMinifyEngine.toMinifier();
            final var jsMinifier = jsMinifyEngine.toMinifier(log);

            final var outputDir = getTargetDirectory(project, targetDir);
            if (!isDirectory(outputDir)) {
                log.warn("Minify target " + outputDir + " directory does not exist.");
                return;
            }

            if (files != null) for (final var file : files) {
                final var path = outputDir.resolve(file);
                if (!isRegularFile(path)) {
                    log.warn("File " + file + " could not be found");
                    continue;
                }

                final var minifier = selectMinifier(path, htmlMinifier, cssMinifier, jsMinifier);
                if (minifier == null) continue;

                tryMinify(log, path, minifier);
            }

            if (directories != null) for (final var dir : directories) {
                final var path = outputDir.resolve(dir);
                if (!isDirectory(path)) {
                    log.warn("Directory " + dir + " could not be found");
                    continue;
                }

                try (final var stream = Files.walk(path)) {
                    stream.filter(Files::isRegularFile).forEach(file -> {
                        if (file.toString().endsWith(".min.js")) return;
                        if (file.toString().endsWith(".min.css")) return;

                        final var minifier = selectMinifier(file, htmlMinifier, cssMinifier, jsMinifier);
                        if (minifier == null) return;
                        tryMinify(log, file, minifier);
                    });
                }
            }
        });
    }

    private static void tryMinify(final Logger log, final Path file, final Minifier minifier) {
        try {
            writeString(file, minifier.minify(readString(file)));
        } catch (final Exception e) {
            log.error("Failed to minify file " + file + ". " + e.getMessage());
        }
    }

    private static Path getTargetDirectory(final MavenProject project, final String targetDir) {
        if (targetDir == null || targetDir.isEmpty())
            return Paths.get(project.getBuild().getOutputDirectory());
        return project.getBasedir().toPath().resolve(targetDir);
    }

    private static Minifier selectMinifier(final Path path, final Minifier htmlMinifier,
                                           final Minifier cssMinifier, final Minifier jsMinifier) {
        final var filename = path.toString();
        if (filename.endsWith(".htm")) return htmlMinifier;
        if (filename.endsWith(".html")) return htmlMinifier;
        if (filename.endsWith(".js")) return jsMinifier;
        if (filename.endsWith(".css")) return cssMinifier;
        return null;
    }

}