package htmlcompiler.checks.jsoup;

import htmlcompiler.checks.jsoup.TagChecker.HeadChecker;
import htmlcompiler.tools.Logger;

public enum StyleAttribute {;

    public static TagChecker newStyleAttribute(final Logger log) {
        return (HeadChecker) (file, element) -> {
            for (final var attribute : element.attributes()) {
                if ("style".equalsIgnoreCase(attribute.getKey())) {
                    log.warn("File " + file.getPath() + " contains a style attribute");
                }
            }
        };
    }

}
