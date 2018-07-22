package htmlcompiler;

import htmlcompiler.model.error.UnrecognizedCommand;
import htmlcompiler.model.error.UnrecognizedFileType;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static htmlcompiler.model.Command.newCommand;
import static htmlcompiler.model.CommandType.toCommandType;
import static htmlcompiler.model.CommandType.unknown;
import static htmlcompiler.model.FileType.fromUserInput;
import static htmlcompiler.tools.InputValidation.orDefault;

public enum Cmd {;
    static {
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.OFF);
    }

    public static void main(final String... args) {
        final Options options = newCommandLineOptions();

        try {
            final CommandLine cmd = new DefaultParser().parse(options, args);
            final File inputDir = new File(orDefault(cmd.getOptionValue("root"), "."));

            newCommand(toCommandType(cmd.getArgs(), unknown), inputDir).execute
                ( inputDir
                , fromUserInput(cmd.getOptionValue("type"))
                , cmd.getOptionValue("input")
                , toOutputStream(cmd.getOptionValue("output"))
                );
        } catch (ParseException | FileNotFoundException | UnrecognizedFileType | IllegalArgumentException | UnrecognizedCommand e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("htmlcompiler [OPTIONS] compile|compress|diff|verify", options);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("An unhandled error occurred.");
            e.printStackTrace();
        }
    }

    private static Options newCommandLineOptions() {
        final Options options = new Options();
        options.addOption(Option.builder("i").longOpt("input").hasArg(true).desc("Input file").required(true).build());
        options.addOption(Option.builder("o").longOpt("output").hasArg(true).desc("Output file").required(false).build());
        options.addOption(Option.builder("t").longOpt("type").hasArg(true).desc("File type").required(false).build());
        options.addOption(Option.builder("r").longOpt("root").hasArg(true).desc("Resource loading root").required(false).build());
        return options;
    }

    private static PrintStream toOutputStream(final String output) throws FileNotFoundException {
        if (output == null || output.isEmpty()) return System.out;
        return new PrintStream(new FileOutputStream(output));
    }

}
