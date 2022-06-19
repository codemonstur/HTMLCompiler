package htmlcompiler.commands;

import htmlcompiler.compilers.Compressor;
import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.compilers.JsCompiler;
import htmlcompiler.pojos.compile.JsCompressionType;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tools.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static htmlcompiler.compilers.TemplateThenCompile.compileDirectories;
import static htmlcompiler.compilers.TemplateThenCompile.newTemplateThenCompile;
import static htmlcompiler.pojos.compile.CompilerConfig.readChecksConfiguration;
import static htmlcompiler.pojos.compile.JsCompressionType.gcc_advanced;
import static htmlcompiler.tools.Strings.isNullOrEmpty;

public enum Compile {;

    public static class CompileCommandConfig {
        public String validation;
        public Path inputDir;
        public Path outputDir;
        public boolean replaceExtension;
        public Map<String, String> variables;
        public Path baseDir;
        public boolean recursive;
        public String jsCompressorType;
        public boolean checksEnabled;
        public boolean deprecatedTagsEnabled;
        public boolean compressionEnabled;
        public boolean htmlCompressionEnabled;
        public boolean cssCompressionEnabled;
        public boolean jsCompressionEnabled;
        public boolean cacheJsCompression;

        public JsCompressionType getJsCompressorType() {
            if (isNullOrEmpty(jsCompressorType)) return gcc_advanced;
            return JsCompressionType.valueOf(jsCompressorType.replace('-', '_'));
        }
    }

    public static void executeCompile(final Logger log, final CompileCommandConfig config) throws IOException {
        final var libs = new LibraryArchive();
        final var checksSettings = readChecksConfiguration(config.validation);
        final var html = new HtmlCompiler(log, config.getJsCompressorType(), libs, checksSettings, config.checksEnabled,
                config.compressionEnabled, config.deprecatedTagsEnabled, config.htmlCompressionEnabled,
                config.cssCompressionEnabled, config.jsCompressionEnabled, config.cacheJsCompression);
        final var ttc = newTemplateThenCompile(log, config.inputDir, config.outputDir, config.replaceExtension, config.variables, html);

        compileDirectories(config.inputDir, ttc, config.recursive);
    }

}
