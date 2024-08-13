package htmlcompiler.services;

import htmlcompiler.utils.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum CssUsage {;

    private static Map<Path, Set<String>> cssClasses = new HashMap<>();

    public static void addCssClasses(final Path path, final String css) {
        final Set<String> classes = cssClasses.computeIfAbsent(path, path1 -> new HashSet<>());
//        classes.addAll(extractCssClasses(css));
    }

    public static void checkClassesUsed(final Logger logger, final Path path, final String htmlContent) {
        for (final var cssClass : cssClasses.get(path)) {
            if (!htmlContent.contains(cssClass)) {
                logger.warn("CSS class '" + cssClass + "' does not appear to be used in file " + path);
            }
        }
    }

}
