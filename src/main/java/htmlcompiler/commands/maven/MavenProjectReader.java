package htmlcompiler.commands.maven;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;

public enum MavenProjectReader {;

    public static Path toInputDirectory(final MavenProject project) throws MojoFailureException {
        final Path inputDir = project.getBasedir().toPath().resolve("src").resolve("main").resolve("websrc");
        if (!exists(inputDir)) throw new MojoFailureException("Input directory must exist: " + inputDir);
        if (!isDirectory(inputDir)) throw new MojoFailureException("Input directory must be a directory");
        return inputDir;
    }

    public static Path toOutputDirectory(final MavenProject project) throws MojoFailureException {
        final Path outputDir = Paths.get(project.getBuild().getOutputDirectory()).resolve("webbin");
        outputDir.toFile().mkdirs();
        if (!exists(outputDir)) throw new MojoFailureException("Output directory must exist: " + outputDir);
        if (!isDirectory(outputDir)) throw new MojoFailureException("Output directory must be a directory");
        return outputDir;
    }

    public static Path toStaticDirectory(final MavenProject project) throws MojoFailureException {
        final Path outputDir = Paths.get(project.getBuild().getOutputDirectory()).resolve("wwwroot");
        outputDir.toFile().mkdirs();
        if (!exists(outputDir))
            throw new MojoFailureException("Output directory must exist: " + outputDir);
        if (!isDirectory(outputDir))
            throw new MojoFailureException("Output directory must be a directory");
        return outputDir;
    }

    public static Map<String, String> newTemplateContext(final MavenProject project) {
        return applyMavenProjectContext(applyEnvironmentContext(new HashMap<>()), project);
    }

    public static Map<String, String> applyMavenProjectContext(final Map<String, String> context, final MavenProject project) {
        for (final Entry<Object, Object> entry : project.getProperties().entrySet()) {
            context.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return context;
    }
    public static Map<String, String> applyEnvironmentContext(final Map<String, String> context) {
        for (final Entry<String, String> entry : System.getenv().entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
        return context;
    }

}
