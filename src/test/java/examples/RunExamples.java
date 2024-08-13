package examples;

import htmlcompiler.commands.Compile.CompileCommandConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static htmlcompiler.commands.Compile.executeCompile;
import static htmlcompiler.utils.Logger.newConsoleLogger;

public class RunExamples {

    public static void main(final String... args) throws IOException {
        //executeCompile(newConsoleLogger(), exampleProject("old"));
        //executeCompile(newConsoleLogger(), exampleProject("jade"));
        executeCompile(newConsoleLogger(), exampleProject("pug"));
    }

    private static CompileCommandConfig exampleProject(final String name) {
        return defaultProject(Paths.get("examples").resolve(name));
    }
    private static CompileCommandConfig defaultProject(final Path projectDir) {
        return compileConfig(projectDir,
            projectDir.resolve("src/main/websrc"),
            projectDir.resolve("target/classes/webbin")
        );
    }
    private static CompileCommandConfig compileConfig(final Path baseDir, final Path inputDir, final Path outputDir) {
        final var config = new CompileCommandConfig();
        config.baseDir = baseDir;
        config.inputDir = inputDir;
        config.outputDir = outputDir;
        config.variables = new HashMap<>();
        config.recursive = true;
        config.replaceExtension = true;
        config.validation = null;
        return config;
    }

}
