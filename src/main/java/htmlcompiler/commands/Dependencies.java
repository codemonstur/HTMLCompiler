package htmlcompiler.commands;

import htmlcompiler.utils.Logger;

import java.io.FileNotFoundException;

import static htmlcompiler.utils.IO.findBinaryInPath;

public enum Dependencies {;

    public static void executeDependencies(final Logger log) {
        testExistenceBinary(log, "TypeScript compiler", "tsc");
        testExistenceBinary(log, "Dart compiler", "dart2js");
        testExistenceBinary(log, "JS++ compiler", "js++");
        testExistenceBinary(log, "Stylus compiler", "stylus");
    }

    private static void testExistenceBinary(final Logger log, final String name, final String binary) {
        try {
            findBinaryInPath(binary);
        } catch (final FileNotFoundException e) {
            log.warn("Unable to find the " + name + ". Binary name is " + binary);
        }
    }

}
