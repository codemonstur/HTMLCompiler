package htmlcompiler.model;

import htmlcompiler.compile.css.CssCompiler;
import htmlcompiler.compile.HtmlCompiler;
import htmlcompiler.error.UnrecognizedCommand;
import htmlcompiler.tools.IO;
import htmlcompiler.tools.Logger;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.PrintStream;

import static htmlcompiler.compile.css.CssCompiler.*;
import static htmlcompiler.compile.js.JsCompiler.compileJavascriptFile;
import static htmlcompiler.compile.js.JsCompiler.compressJavascriptCode;
import static htmlcompiler.compile.js.TypeScriptCompiler.compileTypeScript;
import static htmlcompiler.model.FileType.detectType;
import static htmlcompiler.model.ScriptType.javascript;
import static htmlcompiler.model.StyleType.css;
import static htmlcompiler.tools.Logger.newLogger;

public interface Command {
    void execute(File inputDir, FileType type, String filename, PrintStream out) throws Exception;

    static Command newCommand(final CommandType type) throws MojoFailureException, UnrecognizedCommand {
        final Logger log = newLogger(System.out::println, System.err::println);
        final HtmlCompiler html = new HtmlCompiler(log);

        switch (type) {
            case compile: return newCompile(html);
            case compress: return newCompress(html);
            case diff: return newDiff();
            case verify: return newVerify();
            default: throw new UnrecognizedCommand("Missing command, please specify one of these; compile, compress, diff, verify");
        }
    }

    static Command newVerify() {
        return (inputDir, type, filename, out) -> { };
    }

    static Command newDiff() {
        return (inputDir, type, filename, out) -> { };
    }

    static Command newCompress(final HtmlCompiler html) {
        return (inputDir, type, filename, out) -> {
            if (type == null) type = detectType(filename);

            String output;
            switch (type) {
                case html: output = html.compressHtmlCode(IO.toString(new File(inputDir, filename))); break;
                case stylesheet: output = CssCompiler.compressCssCode(IO.toString(new File(inputDir, filename))); break;
                case javascript: output = compressJavascriptCode(IO.toString(new File(inputDir, filename))); break;
                default: output = "";
            }
            out.print(output);
        };
    }

    static Command newCompile(final HtmlCompiler html) {
        return (inputDir, type, filename, out) -> {
            if (type == null) type = detectType(filename);
            final File sourceFile = new File(inputDir, filename);

            String output;
            switch (type) {
                case html: output = html.compileHtmlFile(sourceFile); break;
                case less: output = compileLessFile(sourceFile); break;
                case sass: output = compileScssFile(sourceFile); break;
                case stylesheet: output = compileCssCode(css, IO.toString(sourceFile)); break;
                case typescript: output = compileTypeScript(sourceFile); break;
                case javascript: output = compileJavascriptFile(javascript, sourceFile); break;
                default: output = "";
            }
            out.print(output);
        };
    }
}

