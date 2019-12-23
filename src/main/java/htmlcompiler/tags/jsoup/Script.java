package htmlcompiler.tags.jsoup;

import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.model.MoveType;
import htmlcompiler.model.ScriptBag;
import htmlcompiler.model.ScriptType;
import htmlcompiler.tools.Logger;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

import static htmlcompiler.compilers.js.JsCompiler.compressJavascriptCode;
import static htmlcompiler.tags.jsoup.TagParsingJsoup.*;
import static htmlcompiler.model.MoveType.storeCode;
import static htmlcompiler.model.MoveType.toMoveType;
import static htmlcompiler.model.ScriptType.detectScriptType;
import static htmlcompiler.model.ScriptType.javascript;
import static htmlcompiler.tools.IO.toLocation;

public enum Script {;

    public static TagVisitor newScriptVisitor(final Logger log, final JsoupCompiler html, final ScriptBag scripts) {
        return (TailVisitor) (file, node, depth) -> {
            if (!node.hasAttr("src") && node.hasAttr("inline"))
                throw new InvalidInput("script inline attempted on tag without src attribute");
            if (node.hasAttr("src") && !isEmpty(node))
                throw new InvalidInput("script tag has both src tag and text content");

            if (!node.hasAttr("src") && isEmpty(node)) {
                node.attr("htmlcompiler", "delete-me");
                return;
            }
            if (node.hasAttr("move")) {
                final MoveType type = toMoveType(node.attr("move"), null);
                final ScriptType scriptType = detectScriptType(node, javascript);
                final String code = compileScriptTag(node, scriptType, file);
                storeCode(compressIfRequested(node, code), type, scripts);
                node.text("");
                node.attr("htmlcompiler", "delete-me");
                return;
            }

            if (!isEmpty(node)) {
                final ScriptType type = detectScriptType(node, null);
                if (type != null) {
                    node.text(compressIfRequested(node, type.compile(node.text(), file)));
                    removeAttributes(node, "inline", "compress", "src", "type");

                    final Element previousSibling = previousElementSibling(node);
                    if (isInlineScript(previousSibling) && !isEmpty(previousSibling)) {
                        node.text(previousSibling.text() + node.text());
                        previousSibling.attr("htmlcompiler", "delete-me");
                    }

                    return;
                }
            }

            if (isHtml(node) && !isEmpty(node)) {
                node.text(html.compileHtmlFragment(file, node.text()).children().html());
                return;
            }

            if (node.hasAttr("inline")) {
                final ScriptType type = detectScriptType(node, javascript);
                final File location = toLocation(file, node.attr("src"), "script tag in %s has an invalid src location '%s'");
                node.text(compressIfRequested(node, type.compile(location)));
                removeAttributes(node, "inline", "compress", "src", "type");

                final Element previousSibling = previousElementSibling(node);
                if (isInlineScript(previousSibling) && !isEmpty(previousSibling)) {
                    node.text(previousSibling.text() + node.text());
                    previousSibling.attr("htmlcompiler", "delete-me");
                }

                return;
            }
            if (node.hasAttr("src") && !node.hasAttr("integrity") && !node.hasAttr("no-integrity")) {
                addIntegrityAttributes(node, node.attr("src"), file, log);
            }
            if (node.hasAttr("to-absolute")) {
                makeAbsolutePath(node, "src");
            }
            removeAttributes(node, "to-absolute", "no-integrity");
        };
    }

    private static String compileScriptTag(final Element element, final ScriptType scriptType, final File parent) throws IOException, InvalidInput {
        if (!isEmpty(element)) return scriptType.compile(element.text(), parent);

        final File location = toLocation(parent, element.attr("src"), "script tag in %s has an invalid src location '%s'");
        return scriptType.compile(location);
    }

    private static String compressIfRequested(final Element element, final String code) throws IOException {
        if (code == null || code.isEmpty()) return code;
        return element.hasAttr("compress") ? compressJavascriptCode(code) : code;
    }

}
