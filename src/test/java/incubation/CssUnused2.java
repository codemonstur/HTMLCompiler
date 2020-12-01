package incubation;

import htmlcompiler.tools.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum CssUnused2 {;

    // Second idea for detection unused CSS classes. Much more direct approach that is
    // likely to work fairly well for code written in my style.
    //
    // It simply checks the existence of the css class name anywhere in the source (after
    // removing the style tag itself). A few css-classes might be flagged as still in use
    // when they aren't but its a start.
    //
    // Ran into the same issue with finding a parser that works with bob. Still don't feel
    // like fixing bob. My guess is the dependencyManagement section isn't loaded correctly
    // for ph-css because it uses an import dependency there.

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
