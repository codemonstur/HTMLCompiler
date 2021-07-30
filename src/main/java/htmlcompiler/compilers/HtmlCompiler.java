package htmlcompiler.compilers;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.pojos.compile.CompilerConfig;
import htmlcompiler.pojos.compile.ScriptBag;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.compilers.tags.TagVisitor;
import htmlcompiler.tools.Logger;
import htmlcompiler.tools.MutableInteger;
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
import java.util.function.BiConsumer;

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
import static htmlcompiler.tools.Filenames.toRelativePath;
import static xmlparser.utils.Functions.isNullOrEmpty;

public final class HtmlCompiler {

    private final Logger log;
    private final HtmlCompressor compressor;
    private final Map<String, TagVisitor> processors;
    private final Map<String, CompilerConfig> configs;
    public final Map<String, MutableInteger> linkCounts = new HashMap<>();

    public HtmlCompiler(final Logger log, final LibraryArchive archive, final Map<String, CompilerConfig> configs) {
        this.log = log;
        this.compressor = newDefaultHtmlCompressor();
        this.processors = newDefaultTagProcessors(log, this, archive);
        this.configs = configs;
    }

    private static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

    private static Map<String, TagVisitor> newDefaultTagProcessors(final Logger log, final HtmlCompiler html, final LibraryArchive archive) {
        final ScriptBag scripts = new ScriptBag();
        final Map<String, TagVisitor> processors = new HashMap<>();
        processors.put("style", newStyleVisitor());
        processors.put("link", newLinkVisitor(log, html));
        processors.put("img", newImageVisitor(html));
        processors.put("script", newScriptVisitor(log, html, scripts));
        processors.put("body", newBodyVisitor(scripts));
        processors.put("head", newHeadVisitor(scripts));
        processors.put("import", newImportVisitor(html));
        processors.put("include", newIncludeVisitor(html));
        processors.put("library", newLibraryVisitor(archive));
        processors.put("meta", newMetaVisitor(archive));
        return processors;
    }

    public String doctypeCompressCompile(final Path file, final String content) throws InvalidInput {
        return "<!DOCTYPE html>"+compressHtmlCode(compileHtmlCode(file, content));
    }

    public String compressHtmlCode(final String content) {
        return compressor.compress(content);
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
                    } catch (Exception e) {
                        errors.add(e);
                    }
                }
            }
            public void tail(final Node node, final int depth) {
                if (node instanceof final Element elem) {
                    try {
                        processors.getOrDefault(node.nodeName(), NOOP).tail(config, file, elem, depth);
                    } catch (Exception e) {
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

        for (final Exception e : errors) {
            log.warn("Error occurred: " + e.getMessage());
        }
        if (!errors.isEmpty()) {
            throw new InvalidInput("HTML failed to compile, fix errors first");
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
