package htmlcompiler;

import htmlcompiler.compiler.CssCompiler;
import htmlcompiler.compiler.HtmlCompiler;
import htmlcompiler.compiler.JsCompiler;
import htmlcompiler.error.UnrecognizedFileType;
import htmlcompiler.logging.Logger;
import htmlcompiler.logging.StdoutLogger;
import htmlcompiler.util.Loader;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static htmlcompiler.Cmd.Command.*;
import static htmlcompiler.Cmd.FileType.*;
import static htmlcompiler.util.InputValidation.orDefault;

public final class Cmd {
    static {
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.OFF);
    }

    private Cmd(){}

    public enum Command {
        compile, compress, diff, verify
    }
    public enum FileType {
        html, stylesheet, less, sass, javascript, typescript
    }
    private static final Map<String, FileType> toType = new HashMap<>();
    static {
        toType.put("html", html);
        toType.put("js", javascript);
        toType.put("javascript", javascript);
        toType.put("less", less);
        toType.put("sass", sass);
        toType.put("scss", sass);
        toType.put("css", stylesheet);
        toType.put("stylesheet", stylesheet);
        toType.put("typescript", typescript);
        toType.put("ts", typescript);
        toType.put("text/html", html);
        toType.put("text/css", stylesheet);
        toType.put("text/less", less);
        toType.put("text/sass", sass);
        toType.put("text/javascript", javascript);
    }

    public interface Execute {
        void command(Command command, FileType type, String input, PrintStream out) throws Exception;
    }

    private static FileType detectType(final String input) throws UnrecognizedFileType {
        if (input == null || input.isEmpty()) throw new UnrecognizedFileType(input);
        if (input.endsWith(".html")) return html;
        if (input.endsWith(".httl")) return html;
        if (input.endsWith(".min.js")) return javascript;
        if (input.endsWith(".js")) return javascript;
        if (input.endsWith(".ts")) return typescript;
        if (input.endsWith(".min.css")) return stylesheet;
        if (input.endsWith(".css")) return stylesheet;
        if (input.endsWith(".less")) return less;
        if (input.endsWith(".sass")) return sass;
        if (input.endsWith(".scss")) return sass;
        throw new UnrecognizedFileType(input);
    }

    public static void main(final String... args) {
        final Options options = createOptions();
        final HelpFormatter formatter = new HelpFormatter();

        try {
            final CommandLine cmd = new DefaultParser().parse(options, args);
            final Command command = toCommand(cmd.getArgs());

            final Logger log = new StdoutLogger();
            final Loader loader = new Loader(orDefault(cmd.getOptionValue("root"), "."));
            final JsCompiler js = new JsCompiler(loader);
            final CssCompiler css = new CssCompiler(loader);
            final HtmlCompiler html = new HtmlCompiler(log, loader, css, js, new HashMap<>());

            createExecutions(loader, html, css, js).get(command).command( command
               , toType.get(cmd.getOptionValue("type"))
               , cmd.getOptionValue("input")
               , toOutputStream(cmd.getOptionValue("output")));

        } catch (ParseException | FileNotFoundException | UnrecognizedFileType | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("htmlcompiler [OPTIONS] compile|compress|diff|verify", options);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("An unhandled error occurred.");
            e.printStackTrace();
        }
    }

    private static Options createOptions() {
        final Options options = new Options();
        options.addOption(Option.builder("i").longOpt("input").hasArg(true).desc("Input file").required(true).build());
        options.addOption(Option.builder("o").longOpt("output").hasArg(true).desc("Output file").required(false).build());
        options.addOption(Option.builder("t").longOpt("type").hasArg(true).desc("File type").required(false).build());
        options.addOption(Option.builder("r").longOpt("root").hasArg(true).desc("Resource loading root").required(false).build());
        return options;
    }

    private static Command toCommand(final String[] args) {
        if (args == null || args.length == 0)
            throw new IllegalArgumentException("Missing command, please specify one of these; compile, compress, diff, verify");
        return Command.valueOf(args[0]);
    }

    private static PrintStream toOutputStream(final String output) throws FileNotFoundException {
        if (output == null || output.isEmpty()) return System.out;
        return new PrintStream(new FileOutputStream(output));
    }

    private static Map<Command, Execute> createExecutions(final Loader loader, final HtmlCompiler html, final CssCompiler css, final JsCompiler js) {
        final Map<Command, Execute> executions = new HashMap<>();
        executions.put(compile, (command, type, input, out) -> {
            if (type == null) type = detectType(input);

            String output;
            switch (type) {
                case html: output = html.compile(new File(loader.getRoot(), input)); break;
                case less: output = css.compile(CssCompiler.Type.LESS, loader.getAsString(input)); break;
                case sass: output = css.compile(CssCompiler.Type.SASS, loader.getAsString(input)); break;
                case stylesheet: output = css.compile(CssCompiler.Type.CSS, loader.getAsString(input)); break;
                case typescript: output = js.compile(JsCompiler.Type.TYPESCRIPT, new File(loader.getRoot()), loader.getAsString(input)); break;
                case javascript: output = js.compile(JsCompiler.Type.JAVASCRIPT, new File(loader.getRoot(), input), loader.getAsString(input)); break;
                default: output = "";
            }
            out.print(output);
        });
        executions.put(compress, (command, type, input, out) -> {
            if (type == null) type = detectType(input);

            String output;
            switch (type) {
                case html: output = html.compress(loader.getAsString(input)); break;
                case stylesheet: output = css.compress(loader.getAsString(input)); break;
                case javascript: output = js.compress(loader.getAsString(input)); break;
                default: output = "";
            }
            out.print(output);
        });
        executions.put(diff, (command, type, input, out) -> {
            // diffing css
            // diffing js
        });
        executions.put(verify, (command, type, input, out) -> {
            // verify html
            // verify js
            // verify css
        });

        return executions;
    }
}
