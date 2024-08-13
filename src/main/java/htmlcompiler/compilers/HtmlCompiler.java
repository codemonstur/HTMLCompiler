package htmlcompiler.compilers;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.compilers.tags.TagVisitor;
import htmlcompiler.pojos.compile.CompilerConfig;
import htmlcompiler.pojos.compile.JsCompressionType;
import htmlcompiler.pojos.compile.ScriptBag;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.utils.Logger;
import htmlcompiler.utils.MutableInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static htmlcompiler.compilers.checks.CheckListBuilder.newJsoupCheckList;
import static htmlcompiler.compilers.tags.Body.newBodyVisitor;
import static htmlcompiler.compilers.tags.Head.newHeadVisitor;
import static htmlcompiler.compilers.tags.Image.newImageVisitor;
import static htmlcompiler.compilers.tags.Import.newImportVisitor;
import static htmlcompiler.compilers.tags.Include.newIncludeVisitor;
import static htmlcompiler.compilers.tags.Library.newLibraryVisitor;
import static htmlcompiler.compilers.tags.Link.newLinkVisitor;
import static htmlcompiler.compilers.tags.Meta.newMetaVisitor;
import static htmlcompiler.compilers.tags.Script.newScriptVisitor;
import static htmlcompiler.compilers.tags.Style.newStyleVisitor;
import static htmlcompiler.compilers.tags.TagVisitor.NOOP;
import static htmlcompiler.services.RepositoryJsCode.cached;
import static htmlcompiler.utils.Filenames.toRelativePath;
import static xmlparser.utils.Functions.isNullOrEmpty;

public final class HtmlCompiler {

    public final Logger log;
    public final HtmlCompressor htmlCompressor;
    public final Compressor cssCompressor;
    public final Compressor jsCompressor;
    public final JsCompressionType jsCompressionType;
    public final Map<String, TagVisitor> processors;
    public final Map<String, CompilerConfig> configs;
    public final Map<String, MutableInteger> linkCounts = new HashMap<>();
    public final Map<String, MutableInteger> cssUtils = new HashMap<>();

    public final boolean checksEnabled;
    public final boolean compressionEnabled;
    public final boolean htmlCompressionEnabled;
    public final boolean cssCompressionEnabled;
    public final boolean jsCompressionEnabled;
    public final boolean deprecatedTagsEnabled;
    public final boolean cachedJsCompression;

    public HtmlCompiler(final Logger log, final JsCompressionType jsCompressionType, final LibraryArchive archive,
                        final Map<String, CompilerConfig> configs, final boolean checksEnabled,
                        final boolean compressionEnabled, final boolean deprecatedTagsEnabled,
                        final boolean htmlCompressionEnabled, final boolean cssCompressionEnabled,
                        final boolean jsCompressionEnabled, final boolean cachedJsCompression) {
        this.log = log;
        this.configs = configs;
        this.checksEnabled = checksEnabled;
        this.compressionEnabled = compressionEnabled;
        this.htmlCompressionEnabled = htmlCompressionEnabled;
        this.cssCompressionEnabled = cssCompressionEnabled;
        this.jsCompressionEnabled = jsCompressionEnabled;
        this.deprecatedTagsEnabled = deprecatedTagsEnabled;
        this.cachedJsCompression = cachedJsCompression;
        this.htmlCompressor = newDefaultHtmlCompressor();
        this.cssCompressor = CssCompiler::compressCssCode;
        this.jsCompressor = jsCompressionType.toCompressor(log);
        this.jsCompressionType = jsCompressionType;
        this.processors = newDefaultTagProcessors(log, this, archive);
    }

    private static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

    private static Map<String, TagVisitor> newDefaultTagProcessors(final Logger log, final HtmlCompiler html
            , final LibraryArchive archive) {
        final ScriptBag scripts = new ScriptBag();
        final Map<String, TagVisitor> processors = new HashMap<>();
        processors.put("style", newStyleVisitor(log, html));
        processors.put("link", newLinkVisitor(log, html));
        processors.put("img", newImageVisitor(html));
        processors.put("script", newScriptVisitor(log, html, scripts));
        if (html.deprecatedTagsEnabled) {
            processors.put("body", newBodyVisitor(scripts));
            processors.put("head", newHeadVisitor(scripts));
            processors.put("import", newImportVisitor(html));
            processors.put("include", newIncludeVisitor(html));
            processors.put("library", newLibraryVisitor(archive));
            processors.put("meta", newMetaVisitor(archive));
        }
        return processors;
    }

    public String doctypeCompressCompile(final Path file, final String code) throws InvalidInput {
        return "<!DOCTYPE html>" + compressHtml(compileHtmlCode(file, code));
    }

    public String compressHtml(final String code) {
        return compressionEnabled && htmlCompressionEnabled ? htmlCompressor.compress(code) : code;
    }
    public String compressCss(final String code) {
        return compressionEnabled && cssCompressionEnabled ? cssCompressor.compress(code) : code;
    }
    public String compressJs(final String code) {
        if (!compressionEnabled || !jsCompressionEnabled) return code;
        return cached(cachedJsCompression, jsCompressionType, code, jsCompressor);
    }

    public String compileHtmlCode(final Path file, final String content) throws InvalidInput {
        return compileAndValidateHtml(file, removeDoctype(Jsoup.parse(content))).html();
    }

    public Element compileHtmlFragment(final Path file, final String content) throws InvalidInput {
        return compileAndValidateHtml(file, Jsoup.parseBodyFragment(content).body());
    }

    private Element compileAndValidateHtml(final Path file, final Element element) throws InvalidInput {
        final var config = findConfigFor(file, configs);

        this.linkCounts.clear();

        final List<Exception> errors = new ArrayList<>();
        element.traverse(new NodeVisitor() {
            public void head(final Node node, final int depth) {
                if (node instanceof final Element elem) {
                    try {
                        processors.getOrDefault(node.nodeName(), NOOP).head(config, file, elem, depth);
                    } catch (final Exception e) {
                        errors.add(e);
                    }
                }
            }
            public void tail(final Node node, final int depth) {
                if (node instanceof final Element elem) {
                    try {
                        processors.getOrDefault(node.nodeName(), NOOP).tail(config, file, elem, depth);
                    } catch (final Exception e) {
                        errors.add(e);
                    }
                }
            }
        });
        element.select("*[htmlcompiler=delete-me]").remove();

        linkCounts.forEach((link, count) -> {
            if (count.getValue() > 1)
                log.warn("File " + toRelativePath(file) + " contains " + count.getValue() + " entries to " + toRelativePath(link));
        });
        cssUtils.forEach((util, count) -> {
            if (count.getValue() > 1)
                log.warn("CSS-util " + util + " is imported more than once");
        });

        if (checksEnabled) {
            final var checks = newJsoupCheckList(config).addAllEnabled().build();
            element.traverse(new NodeVisitor() {
                public void head(final Node node, final int depth) {
                    if (node instanceof final Element element) {
                        for (final var check : checks) check.checkElement(log, config, file, element);
                        for (final var siblings : config.validator.siblingAttributes.entrySet()) {
                            if (!isNullOrEmpty(element.attr(siblings.getKey())) && isNullOrEmpty(element.attr(siblings.getValue())))
                                log.warn("File " + toRelativePath(file) + " has a tag '" + element.tagName() + "' with an attribute '" + siblings.getKey() + "' but not '" + siblings.getValue() + "'");
                        }
                    }
                    if (node instanceof TextNode) {
                        final var element = (Element) node.parent();
                        for (final String attribute : config.validator.textNodeParentsHaveAttributes) {
                            if (isNullOrEmpty(element.attr(attribute)))
                                log.warn("File " + toRelativePath(file) + " contains a text node '" + ((TextNode) node).text() + "' with missing parent attribute '" + attribute + "'");
                        }
                    }
                }
                public void tail(final Node node, final int depth) {}
            });
        }

        for (final Exception e : errors) {
            log.warn(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        if (!errors.isEmpty()) {
            throw new InvalidInput("HTML failed to compile, fix errors first", errors.get(0));
        }

        return element;
    }

    private static CompilerConfig findConfigFor(final Path file, final Map<String, CompilerConfig> configs) {
        final var config = configs.get(file.getFileName().toString());
        return config != null ?  config : configs.get("");
    }

    private static Document removeDoctype(final Document document) {
        final Node node = document.childNode(0);
        if ("#doctype".equals(node.nodeName())) {
            node.remove();
        }
        return document;
    }

}
