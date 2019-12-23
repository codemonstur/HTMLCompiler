package htmlcompiler.compilers.html;

import com.google.gson.Gson;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.checks.jsoup.JsoupElementChecks;
import htmlcompiler.checks.jsoup.JsoupElementChecks.JsoupElementCheck;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.library.LibraryArchive;
import htmlcompiler.model.ScriptBag;
import htmlcompiler.tags.jsoup.TagVisitor;
import htmlcompiler.tools.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public final class JsoupCompiler implements HtmlCompiler {

    private final Logger log;
    private final HtmlCompressor compressor;
    private final Map<String, TagVisitor> processors;
    private final List<JsoupElementCheck> checkers;

    public JsoupCompiler(final Logger log) {
        this.log = log;
        this.compressor = newDefaultHtmlCompressor();
        this.processors = newDefaultTagProcessors(log, this, new LibraryArchive(new Gson()));
        this.checkers = newDefaultTagCheckers();
    }

    private static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
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
    private static List<JsoupElementCheck> newDefaultTagCheckers() {
        return List.of
            ( JsoupElementChecks::dontUseMarquee
            , JsoupElementChecks::dontUseBlink
            , JsoupElementChecks::hasBorderAttribute
            , JsoupElementChecks::hasStyleAttribute
            , JsoupElementChecks::hasUppercaseTagsOrAttributes
            , JsoupElementChecks::missingAltForImages
            , JsoupElementChecks::missingPlaceholderForInputs
            , JsoupElementChecks::dontUseBold
            , JsoupElementChecks::dontUseItalic
            , JsoupElementChecks::dontUseStrong
            , JsoupElementChecks::dontUseEm
            , JsoupElementChecks::dontUseStyling
            , JsoupElementChecks::marginWidthInBody
            , JsoupElementChecks::alignAttributeContainsAbsmiddle
            , JsoupElementChecks::hasDeprecatedTag
            , JsoupElementChecks::hasDeprecatedAttribute
            , JsoupElementChecks::scriptWithHardcodedNonce
            , JsoupElementChecks::styleWithHardcodedNonce
            , JsoupElementChecks::labelWithForAttribute
            , JsoupElementChecks::inputWithoutMaxLength
            , JsoupElementChecks::hasEventHandlerAttribute
            , JsoupElementChecks::isValidTag
            , JsoupElementChecks::isValidAttribute
            );
    }

    public String doctypeCompressCompile(final File file, final String content) throws InvalidInput {
        return "<!DOCTYPE html>"+compressHtmlCode(compileHtmlCode(file, content));
    }

    public String compressHtmlCode(final String content) {
        return compressor.compress(content);
    }

    public String compileHtmlCode(final File file, final String content) throws InvalidInput {
        return applyVisitors(file, removeDoctype(Jsoup.parse(content))).html();
    }

    public Element compileHtmlFragment(final File file, final String content) throws InvalidInput {
        return applyVisitors(file, Jsoup.parseBodyFragment(content).body());
    }

    private Element applyVisitors(final File file, final Element element) throws InvalidInput {
        final List<Exception> errors = new ArrayList<>();
        element.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node instanceof Element) {
                    final Element elem = (Element) node;
                    try {
                        processors.getOrDefault(node.nodeName(), NOOP).head(file, elem, depth);
                    } catch (Exception e) {
                        errors.add(e);
                    }
                }
            }
            public void tail(Node node, int depth) {
                if (node instanceof Element) {
                    final Element elem = (Element) node;
                    try {
                        processors.getOrDefault(node.nodeName(), NOOP).tail(file, elem, depth);
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
                    for (final var checker : checkers) checker.checkElement(log, file, elem);
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
