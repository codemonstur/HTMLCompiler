package htmlcompiler.commands;

import com.google.gson.Gson;
import htmlcompiler.pojos.compile.CompilerType;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tools.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

import static htmlcompiler.compilers.TemplateThenCompile.compileDirectories;
import static htmlcompiler.compilers.TemplateThenCompile.newTemplateThenCompile;
import static htmlcompiler.pojos.compile.ChecksConfig.readChecksConfiguration;
import static htmlcompiler.tools.Logger.YYYY_MM_DD_HH_MM_SS;
import static java.lang.String.format;

public enum Compile {;

    public static class CompileCommandConfig {
        public String validation;
        public CompilerType type;
        public Path inputDir;
        public Path outputDir;
        public boolean replaceExtension;
        public Map<String, String> variables;
        public Path baseDir;
        public boolean recursive;
    }

    public static void executeCompile(final Logger log, final CompileCommandConfig config) throws IOException {
        final var gson = new Gson();
        final var libs = new LibraryArchive(gson);
        final var checksSettings = readChecksConfiguration(config.validation, gson);
        final var html = config.type.newHtmlCompiler(log, libs, checksSettings);
        final var ttc = newTemplateThenCompile(config.inputDir, config.outputDir, config.replaceExtension, config.variables, html);

        log.info(format
            ( "[%s] Compiling supported template formats in %s to %s"
            , LocalDateTime.now().format(YYYY_MM_DD_HH_MM_SS)
            , config.baseDir.relativize(config.inputDir)
            , config.baseDir.relativize(config.outputDir)
            ));

        compileDirectories(config.inputDir, ttc, config.recursive);

    }

}
