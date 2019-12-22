package htmlcompiler.checks.jsoup;

import htmlcompiler.checks.jsoup.TagChecker.HeadChecker;
import htmlcompiler.tools.Logger;

public enum UppercaseTagsOrAttributes {;

    public static TagChecker newUppercaseTagsOrAttributes(final Logger log) {
        return (HeadChecker) (file, element) -> {
            if (hasUppercase(element.nodeName())) {
                log.warn("File " + file.getPath() + " contains a tag " + element.nodeName() + " with upper case letters");
            }

            for (final var attribute : element.attributes()) {
                if (hasUppercase(attribute.getKey())) {
                    log.warn("File " + file.getPath() + " contains an attribute " + attribute.getKey() + " with upper case letters");
                }
            }
        };
    }

    private static boolean hasUppercase(final String value) {
        for (final char c : value.toCharArray()) {
            if (Character.isUpperCase(c))
                return true;
        }
        return false;
    }

}
