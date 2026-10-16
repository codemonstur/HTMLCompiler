package htmlcompiler;

import bobthebuildtool.pojos.buildfile.Project;
import htmlcompiler.commands.Compile.CompileCommandConfig;
import jcli.annotations.CliOption;
import jcli.errors.InvalidCommandLine;

import java.util.Map;

import static jcli.CliParserBuilder.newCliParser;

public class CliHtmlCompile {

    @CliOption(name = 'c', longName = "validation-config", defaultValue = "src/main/webcnf/validation.json")
    public String validation;
    @CliOption(name = 'e', longName = "keep-extension")
    public boolean keepExtensions;
    @CliOption(name = 'w', longName = "dont-walk-tree")
    public boolean dontWalkTree;
    @CliOption(name = 'j', longName = "js-compressor", defaultValue = "gcc-simple", description = "Options: gcc-simple, gcc-bundle, gcc-whitespace, gcc-advanced, yui")
    public String jsCompiler;

    @CliOption(longName = "no-linting", description = "Disable checks on html tag validity")
    public boolean disableLinting;
    @CliOption(longName = "deprecated-tags", description = "Enables deprecated tags")
    public boolean deprecatedTags;
    @CliOption(longName = "no-compression", description = "Disable all forms of compression: html, css, js")
    public boolean disableCompression;
    @CliOption(longName = "no-html-compression", description = "Disable HTML compression")
    public boolean disableHtmlCompression;
    @CliOption(longName = "no-css-compression", description = "Disable CSS compression")
    public boolean disableCssCompression;
    @CliOption(longName = "no-js-compression", description = "Disable JS compression")
    public boolean disableJsCompression;
    @CliOption(longName = "js-caching", description = "Enables compressed JavaScript caching")
    public boolean jsCaching;

    public static CompileCommandConfig newCompileCommandConfig(final Project project, final Map<String, String> environment, final String[] args) throws InvalidCommandLine {
        final CliHtmlCompile arguments = newCliParser(CliHtmlCompile::new).parse(args);

        final var config = new CompileCommandConfig();

        config.inputDir = project.parentDir.resolve("src").resolve("main").resolve("websrc");
        config.outputDir = project.getBuildTarget().resolve("classes").resolve("webbin");
        config.variables = environment;
        config.baseDir = project.parentDir;
        config.replaceExtension = !arguments.keepExtensions;
        config.validation = arguments.validation;
        config.recursive = !arguments.dontWalkTree;
        config.jsCompressorType =  arguments.jsCompiler;
        config.checksEnabled = !arguments.disableLinting;
        config.compressionEnabled = !arguments.disableCompression;
        config.htmlCompressionEnabled = !arguments.disableHtmlCompression;
        config.cssCompressionEnabled = !arguments.disableCssCompression;
        config.jsCompressionEnabled = !arguments.disableJsCompression;
        config.deprecatedTagsEnabled = arguments.deprecatedTags;
        config.cacheJsCompression = arguments.jsCaching;

        return config;
    }

}
