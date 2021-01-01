package unittests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.resourceToString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCssParser {

    private static final String
        CSS_COMMENTS = "/css/comments.css",
        CSS_MEDIA = "/css/media.css",
        CSS_SELECTORS = "/css/selectors.css",
        CSS_SIMPLE = "/css/simple.css";

    public static void main(final String... args) {
        System.out.println(extractCssSelectors(""));
        System.out.println(extractCssSelectors("html{margin: 0;}/* sdfdsfd */"));

        System.out.println(removeCssComments("before/*inside*/after"));
        System.out.println(removeCssComments("before/*/inside"));
        System.out.println(removeCssComments("/*/inside*/after"));
        System.out.println(removeCssComments("/*/inside*/"));
        System.out.println(removeCssComments("/"));
        System.out.println(removeCssComments("/*"));
        System.out.println(removeCssComments("before/"));
        System.out.println(removeCssComments("before/*"));
    }

    @Test
    public void testCssComments() throws IOException {
        final var cssCode = resourceToString(CSS_COMMENTS, UTF_8);

        final var cssClasses = extractCssSelectors(cssCode);

        assertEquals(Set.of("p"), cssClasses);
    }

    @Test
    public void testCssMedia() throws IOException {
        final var cssCode = resourceToString(CSS_MEDIA, UTF_8);

        final var cssClasses = extractCssSelectors(cssCode);

        assertEquals(Set.of("p"), cssClasses);
    }

    @Test
    public void testCssSelectors() throws IOException {
        final var cssCode = resourceToString(CSS_SELECTORS, UTF_8);

        final var cssClasses = extractCssSelectors(cssCode);

        assertEquals(Set.of("p"), cssClasses);
    }

    @Test
    public void testCssSimple() throws IOException {
        final var cssCode = resourceToString(CSS_SIMPLE, UTF_8);

        final var cssClasses = extractCssSelectors(cssCode);

        assertEquals(Set.of("body"), cssClasses);
    }

    public static Set<String> extractCssSelectors(final String css) {
        return extractCssSelectors(removeCssComments(css).toCharArray());
    }

    private static Set<String> extractCssSelectors(final char[] chars) {
        final var selectors = new HashSet<String>();

        for (int offset = 0; offset < chars.length;) {
            int openingBracket = findFirst('{', chars, offset);
            selectors.add(new String(chars, offset, openingBracket-offset).trim());
            int closingBracket = findFirst('}', chars, openingBracket+1);
            offset = closingBracket+1;
        }

        return selectors;
    }

    private static String removeCssComments(final String css) {
        final var builder = new StringBuilder();

        final char[] chars = css.toCharArray();
        for (int offset = 0; offset < chars.length;) {
            int startComment = findFirst('/', chars, offset);
            if (startComment == chars.length) {
                builder.append(chars, offset, startComment-offset);
                break;
            }
            if (startComment+1 == chars.length) {
                builder.append(chars, offset, startComment-offset+1);
                break;
            }
            if (chars[startComment+1] != '*') {
                builder.append(chars, offset, startComment-offset);
                offset = startComment+1;
                continue;
            }

            builder.append(chars, offset, startComment-offset);

            int endComment = startComment+2;
            while (endComment+1 < chars.length && chars[endComment] != '*' && chars[endComment+1] != '/')
                endComment = findFirst('*', chars, endComment+1);

            if (endComment+1 >= chars.length) {
                break;
            }

            offset = endComment+2;
        }

        return builder.toString();
    }

    private static int findFirst(final char c, final char[] chars, int index) {
        while (index < chars.length && chars[index] != c) index++;
        return index;
    }

}
