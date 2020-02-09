package htmlcompiler.commands;

import htmlcompiler.tools.Logger;

import java.io.FileNotFoundException;

import static htmlcompiler.tools.IO.findBinaryInPath;

public enum Dependencies {;

    public static void executeDependencies(final Logger log) {
        testExistenceBinary(log, "TypeScript compiler", "tsc");
        testExistenceBinary(log, "Dart compiler", "dart2js");
        testExistenceBinary(log, "JS++ compiler", "js++");
        testExistenceBinary(log, "Stylus compiler", "stylus");
        testExistenceBinary(log, "Sass/Scss compiler", "sass");
    }

    private static void testExistenceBinary(final Logger log, final String name, final String binary) {
        try {
            findBinaryInPath(binary);
        } catch (FileNotFoundException e) {
            log.warn("Unable to find the " + name + ". Binary name is " + binary);
        }
    }

}
