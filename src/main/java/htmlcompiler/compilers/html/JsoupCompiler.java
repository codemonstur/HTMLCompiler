package htmlcompiler.compilers.html;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.checks.jsoup.JsoupElementChecks.JsoupElementCheck;
import htmlcompiler.pojos.compile.Config;
import htmlcompiler.pojos.compile.ScriptBag;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tags.jsoup.TagVisitor;
import htmlcompiler.tools.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.nio.file.Path;
import java.util.*;

import static htmlcompiler.checks.jsoup.JsoupCheckListBuilder.newJsoupCheckList;
import static htmlcompiler.compilers.html.HtmlCompiler.newDefaultHtmlCompressor;
import static htmlcompiler.tags.jsoup.Body.newBodyVisitor;
import static htmlcompiler.tags.jsoup.Head.newHeadVisitor;
import static htmlcompiler.tags.jsoup.Image.newImageVisitor;
import static htmlcompiler.tags.jsoup.Import.newImportVisitor;
import static htmlcompiler.tags.jsoup.Include.newIncludeVisitor;
import static htmlcompiler.tags.jsoup.Library.newLibraryVisitor;
import static htmlcompiler.tags.jsoup.Link.newLinkVisitor;
import static htmlcompiler.tags.jsoup.Meta.newMetaVisitor;
import static htmlcompiler.tags.jsoup.Script.newScriptVisitor;
import static htmlcompiler.tags.jsoup.Style.newStyleVisitor;
import static htmlcompiler.tags.jsoup.TagVisitor.NOOP;
import static xmlparser.utils.Functions.isNullOrEmpty;

public final class JsoupCompiler implements HtmlCompiler {

    private final Logger log;
    private final HtmlCompressor compressor;
    private final Map<String, TagVisitor> processors;
    private final List<JsoupElementCheck> checkers;
    private final Config config;

    public JsoupCompiler(final Logger log, final LibraryArchive archive, final Config configuration) {
        this.log = log;
        this.compressor = newDefaultHtmlCompressor();
        this.processors = newDefaultTagProcessors(log, this, archive);
        this.checkers = newJsoupCheckList(configuration)
            .addAllEnabled()
            .build();
        this.config = configuration;
    }

    private static Map<String, TagVisitor> newDefaultTagProcessors(final Logger log, final JsoupCompiler html, final LibraryArchive archive) {
        final ScriptBag scripts = new ScriptBag();
        final Map<String, TagVisitor> processors = new HashMap<>();
        processors.put("style", newStyleVisitor());
        processors.put("link", newLinkVisitor(log));
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
        return applyVisitors(file, removeDoctype(Jsoup.parse(content))).html();
    }

    public Element compileHtmlFragment(final Path file, final String content) throws InvalidInput {
        return applyVisitors(file, Jsoup.parseBodyFragment(content).body());
    }

    private Element applyVisitors(final Path file, final Element element) throws InvalidInput {
        final List<Exception> errors = new ArrayList<>();
        element.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node instanceof Element) {
                    final Element elem = (Element) node;
                    try {
                        processors.getOrDefault(node.nodeName(), NOOP).head(config, file, elem, depth);
                    } catch (Exception e) {
                        errors.add(e);
                    }
                }
            }
            public void tail(Node node, int depth) {
                if (node instanceof Element) {
                    final Element elem = (Element) node;
                    try {
                        processors.getOrDefault(node.nodeName(), NOOP).tail(config, file, elem, depth);
                    } catch (Exception e) {
                        errors.add(e);
                    }
                }
            }
        });
        element.select("*[htmlcompiler=delete-me]").remove();
        element.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node instanceof Element) {
                    final Element elem = (Element) node;
                    for (final var checker : checkers) checker.checkElement(log, config, file, elem);
                    for (final var siblings : config.validator.siblingAttributes.entrySet()) {
                        if (elem.hasAttr(siblings.getKey()) && isNullOrEmpty(elem.attr(siblings.getValue())))
                            log.warn("File " + file + " has a tag '" + elem.tagName() + "' with an attribute '" + siblings.getKey() + "' + but not '" + siblings.getValue() + "'");
                    }
                }
                if (node instanceof TextNode) {
                    final Element elem = (Element) node.parent();
                    for (final String attribute : config.validator.textNodeParentsHaveAttributes) {
                        if (isNullOrEmpty(elem.attr(attribute)))
                            log.warn("File " + file + " contains a text node '" + ((TextNode) node).text() + "' with missing parent attribute '" + attribute + "'");
                    }
                }
            }
            public void tail(Node node, int depth) {}
        });

        for (final Exception e : errors) {
            log.warn("Error occurred: " + e.getMessage());
        }
        if (!errors.isEmpty()) {
            throw new InvalidInput("HTML failed to compile, fix errors first");
        }

        return element;
    }

    private static Document removeDoctype(final Document document) {
        final Node node = document.childNode(0);
        if ("#doctype".equals(node.nodeName())) {
            node.remove();
        }
        return document;
    }

}
