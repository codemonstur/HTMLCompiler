package htmlcompiler.compilers.tags;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.compilers.tags.TagVisitor.TailVisitor;
import htmlcompiler.pojos.compile.MoveType;
import htmlcompiler.pojos.compile.ScriptBag;
import htmlcompiler.pojos.compile.ScriptType;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.tools.Logger;
import htmlcompiler.tools.MutableInteger;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import static htmlcompiler.compilers.JsCompiler.compressJavascriptCode;
import static htmlcompiler.compilers.tags.TagParsing.*;
import static htmlcompiler.pojos.compile.MoveType.storeCode;
import static htmlcompiler.pojos.compile.MoveType.toMoveType;
import static htmlcompiler.pojos.compile.ScriptType.detectScriptType;
import static htmlcompiler.pojos.compile.ScriptType.javascript;
import static htmlcompiler.services.RepositoryVersions.checkVersionLibrary;
import static htmlcompiler.tools.IO.toLocation;

public enum Script {;

    public static TagVisitor newScriptVisitor(final Logger log, final HtmlCompiler html, final ScriptBag scripts) {
        return (TailVisitor) (config, file, node, depth) -> {
            if (!node.hasAttr("src") && node.hasAttr("inline"))
                throw new InvalidInput("script inline attempted on tag without src attribute");
            if (node.hasAttr("src") && !isScriptEmpty(node))
                throw new InvalidInput("script tag has both src tag and text content");

            if (!node.hasAttr("src") && isScriptEmpty(node)) {
                node.attr("htmlcompiler", "delete-me");
                return;
            }
            if (node.hasAttr("move")) {
                final MoveType type = toMoveType(node.attr("move"), null);
                final ScriptType scriptType = detectScriptType(node, javascript);
                final String code = compileScriptTag(node, scriptType, file);
                storeCode(compressIfRequested(log, node, code), type, scripts);
                setData(node, "");
                node.attr("htmlcompiler", "delete-me");
                return;
            }

            if (node.hasAttr("src") && isScriptEmpty(node))
                checkVersionLibrary(log, file.toString(), node.attr("src"), config.ignoreMajorVersions);

            if (!isScriptEmpty(node)) {
                final ScriptType type = detectScriptType(node, null);
                if (type != null) {
                    setData(node, compressIfRequested(log, node, type.compile(node.data(), file)));
                    removeAttributes(node, "inline", "compress", "src", "type");

                    final Element previousSibling = previousElementSibling(node);
                    if (isInlineScript(previousSibling) && !isScriptEmpty(previousSibling)) {
                        setData(node, previousSibling.data() + node.data());
                        previousSibling.attr("htmlcompiler", "delete-me");
                    }

                    return;
                }
            }

            if (isHtml(node) && !isScriptEmpty(node)) {
                final String compiled = html.compileHtmlFragment(file, node.data()).html();
                final String result = node.hasAttr("compress")
                        ? html.compressHtmlCode(compiled) : compiled;
                removeAttributes(node, "inline", "compress");
                setData(node, result);
                return;
            }

            if (node.hasAttr("inline")) {
                final ScriptType type = detectScriptType(node, javascript);
                final Path location = toLocation(file, node.attr("src"), "script tag in %s has an invalid src location '%s'");
                html.linkCounts.computeIfAbsent(location.toAbsolutePath().toString(), s -> new MutableInteger()).increment();
                setData(node, compressIfRequested(log, node, type.compile(location)));
                removeAttributes(node, "inline", "compress", "src", "type");

                final Element previousSibling = previousElementSibling(node);
                if (isInlineScript(previousSibling) && !isScriptEmpty(previousSibling)) {
                    setData(node, previousSibling.data() + node.data());
                    previousSibling.attr("htmlcompiler", "delete-me");
                }

                return;
            }
            if (node.hasAttr("src") && !node.hasAttr("integrity") && !node.hasAttr("no-integrity")) {
                addIntegrityAttributes(node, node.attr("src"), log);
            }
            if (node.hasAttr("to-absolute")) {
                makeAbsolutePath(node, "src");
            }
            removeAttributes(node, "to-absolute", "no-integrity");
        };
    }

    private static String compileScriptTag(final Element element, final ScriptType scriptType, final Path parent) throws Exception {
        if (!isScriptEmpty(element)) return scriptType.compile(element.data(), parent);

        final Path location = toLocation(parent, element.attr("src"), "script tag in %s has an invalid src location '%s'");
        return scriptType.compile(location);
    }

    private static String compressIfRequested(final Logger log, final Element element, final String code) throws IOException {
        if (code == null || code.isEmpty()) return code;
        return element.hasAttr("compress") ? compressJavascriptCode(log, code) : code;
    }

}
