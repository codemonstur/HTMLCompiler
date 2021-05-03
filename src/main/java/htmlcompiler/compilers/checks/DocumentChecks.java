package htmlcompiler.compilers.checks;

import htmlcompiler.tools.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.file.Path;

import static htmlcompiler.tools.Filenames.toRelativePath;
import static xmlparser.utils.Functions.isNullOrEmpty;

public enum DocumentChecks {;

    public interface JsoupDocumentCheck {
        void head(Logger log, File file, Document document);
    }

    public static void missingPrintingStylesheet(final Logger log, final Path file, final Document document) {
        for (final var link : document.select("link")) {
            if ("stylesheet".equals(link.attr("rel")) && "print".equals(link.attr("media")))
                return;
        }
        log.warn("File " + toRelativePath(file) + " does not have a stylesheet for print media");
    }

    public static void hasSeoMetaTags(final Logger log, final Path file, final Document document) {

        boolean hasDescription = false;
        boolean hasRobots = false;
        boolean hasOpenGraphDescription = false;
        boolean hasOpenGraphImage = false;
        boolean hasOpenGraphTitle = false;
        boolean hasOpenGraphType = false;
        boolean hasOpenGraphUrl = false;

        for (final var meta : document.select("meta")) {
            if ("robots".equalsIgnoreCase(meta.attr("name")) && !isNullOrEmpty(meta.attr("content"))) {
                hasRobots = true;
                continue;
            }
            if ("description".equalsIgnoreCase(meta.attr("name")) && !isNullOrEmpty(meta.attr("content"))) {
                hasDescription = true;
                continue;
            }
            if ("og:description".equalsIgnoreCase(meta.attr("name")) && !isNullOrEmpty(meta.attr("content"))) {
                hasOpenGraphDescription = true;
                continue;
            }
            if ("og:image".equalsIgnoreCase(meta.attr("name")) && !isNullOrEmpty(meta.attr("content"))) {
                hasOpenGraphImage = true;
                continue;
            }
            if ("og:title".equalsIgnoreCase(meta.attr("name")) && !isNullOrEmpty(meta.attr("content"))) {
                hasOpenGraphTitle = true;
                continue;
            }
            if ("og:type".equalsIgnoreCase(meta.attr("name")) && !isNullOrEmpty(meta.attr("content"))) {
                hasOpenGraphType = true;
                continue;
            }
            if ("og:url".equalsIgnoreCase(meta.attr("name")) && !isNullOrEmpty(meta.attr("content"))) {
                hasOpenGraphUrl = true;
                continue;
            }
        }

        if (!hasDescription)
            log.warn("File " + toRelativePath(file) + " doesn't have a description meta tag");
        if (!hasRobots)
            log.warn("File " + toRelativePath(file) + " doesn't have a robots meta tag. Consider; index, follow, noindex, nofollow, noimageindex, noarchive, nosnippet, notranslate");
        if (!hasOpenGraphDescription)
            log.warn("File " + toRelativePath(file) + " doesn't have an og:description meta tag");
        if (!hasOpenGraphImage)
            log.warn("File " + toRelativePath(file) + " doesn't have an og:image meta tag");
        if (!hasOpenGraphTitle)
            log.warn("File " + toRelativePath(file) + " doesn't have an og:title meta tag");
        if (!hasOpenGraphType)
            log.warn("File " + toRelativePath(file) + " doesn't have an og:type meta tag");
        if (!hasOpenGraphUrl)
            log.warn("File " + toRelativePath(file) + " doesn't have an og:url meta tag");

    }

    public static void hasViewportMetaTag(final Logger log, final Path file, final Document document) {
        final Element element = document.selectFirst("meta[name=viewport]");
        if (element == null || isNullOrEmpty(element.attr("content")))
            log.warn("File " + toRelativePath(file) + " doesn't have a viewport meta tag");
    }

    public static void hasTitleTag(final Logger log, final Path file, final Document document) {
        if (document.selectFirst("title") == null)
            log.warn("File " + toRelativePath(file) + " doesn't have a title tag");
    }

}
