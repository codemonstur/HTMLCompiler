package htmlcompiler.commands;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tools.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static htmlcompiler.compilers.TemplateThenCompile.compileDirectories;
import static htmlcompiler.compilers.TemplateThenCompile.newTemplateThenCompile;
import static htmlcompiler.pojos.compile.CompilerConfig.readChecksConfiguration;

public enum Compile {;

    public static class CompileCommandConfig {
        public String validation;
        public Path inputDir;
        public Path outputDir;
        public boolean replaceExtension;
        public Map<String, String> variables;
        public Path baseDir;
        public boolean recursive;
    }

    public static void executeCompile(final Logger log, final CompileCommandConfig config) throws IOException {
        final var libs = new LibraryArchive();
        final var checksSettings = readChecksConfiguration(config.validation);
        final var html = new HtmlCompiler(log, libs, checksSettings);
        final var ttc = newTemplateThenCompile(log, config.inputDir, config.outputDir, config.replaceExtension, config.variables, html);

        compileDirectories(config.inputDir, ttc, config.recursive);
    }

}
