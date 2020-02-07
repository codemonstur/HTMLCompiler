package htmlcompiler.tags.neko;

import htmlcompiler.compilers.html.NekoCompiler;
import htmlcompiler.pojos.compile.MoveType;
import htmlcompiler.pojos.compile.ScriptBag;
import htmlcompiler.pojos.compile.ScriptType;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.tools.Logger;
import org.w3c.dom.Element;
import simplexml.SimpleXml;
import simplexml.utils.Interfaces.CheckedIterator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static htmlcompiler.compilers.scripts.JsCompiler.compressJavascriptCode;
import static htmlcompiler.pojos.compile.MoveType.storeCode;
import static htmlcompiler.pojos.compile.MoveType.toMoveType;
import static htmlcompiler.pojos.compile.ScriptType.detectScriptType;
import static htmlcompiler.pojos.compile.ScriptType.javascript;
import static htmlcompiler.tags.neko.TagParsingNeko.*;
import static htmlcompiler.tools.IO.toLocation;
import static java.nio.charset.StandardCharsets.UTF_8;

public enum Script {;

    public static TagProcessor newScriptProcessor(final Logger log, final NekoCompiler html, final SimpleXml xml, final ScriptBag scripts) {
        return (file, document, element) -> {

            if (!element.hasAttribute("src") && element.hasAttribute("inline"))
                throw new InvalidInput("script inline attempted on tag without src attribute");
            if (element.hasAttribute("src") && !isEmpty(element))
                throw new InvalidInput("script tag has both src tag and text content");

            if (!element.hasAttribute("src") && isEmpty(element)) {
                deleteTag(element);
                return true;
            }
            if (element.hasAttribute("move")) {
                final MoveType type = toMoveType(element.getAttribute("move"), null);
                final ScriptType scriptType = detectScriptType(element, javascript);
                final String code = compileScriptTag(element, scriptType, file);
                storeCode(compressIfRequested(element, code), type, scripts);
                deleteTag(element);
                return true;
            }

            if (!isEmpty(element)) {
                final ScriptType type = detectScriptType(element, null);
                if (type != null) {
                    element.setTextContent(compressIfRequested(element, type.compile(element.getTextContent(), file)));
                    removeAttributes(element, "inline", "compress", "src", "type");

/*
                    This code is supposed to merge adjacent tags together. It does not work.

                    final Node previousSibling = getPreviousTagSibling(element, null);
                    if (isInlineScript(previousSibling) && !isEmpty(previousSibling)) {
                        element.setTextContent(previousSibling.getTextContent() + element.getTextContent());
                        element.getParentNode().removeChild(previousSibling);
                    }
*/

                    return false;
                }
            }

            if (isHtml(element) && !isEmpty(element)) {
                final CheckedIterator<String> it = xml.iterateXml(new ByteArrayInputStream(element.getTextContent().getBytes(UTF_8)));
                final StringBuilder builder = new StringBuilder();
                while (it.hasNext()) {
                    builder.append(html.compileHtmlCode(file, it.next()));
                }
                final String result = element.hasAttribute("compress")
                    ? html.compressHtmlCode(builder.toString()) : builder.toString();

                element.setTextContent(result);
                removeAttributes(element, "inline", "compress");
                return false;
            }

            if (element.hasAttribute("inline")) {
                final ScriptType type = detectScriptType(element, javascript);
                final Path location = toLocation(file, element.getAttribute("src"), "script tag in %s has an invalid src location '%s'");
                element.setTextContent(compressIfRequested(element, type.compile(location)));
                removeAttributes(element, "inline", "compress", "src", "type");

/*
                This code is supposed to merge adjacent tags together. It does not work.

                final Node previousSibling = getPreviousTagSibling(element, null);
                if (isInlineScript(previousSibling) && !isEmpty(previousSibling)) {
                    element.setTextContent(previousSibling.getTextContent() + element.getTextContent());
                    element.getParentNode().removeChild(previousSibling);
                }
*/

                return false;
            }
            if (element.hasAttribute("src") && !element.hasAttribute("integrity") && !element.hasAttribute("no-integrity")) {
                addIntegrityAttributes(element, element.getAttribute("src"), file, html, log);
            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
            removeAttributes(element, "to-absolute", "no-integrity");
            return false;
        };
    }

    private static String compileScriptTag(final Element element, final ScriptType scriptType, final Path parent) throws Exception {
        if (!isEmpty(element)) return scriptType.compile(element.getTextContent(), parent);

        final Path location = toLocation(parent, element.getAttribute("src"), "script tag in %s has an invalid src location '%s'");
        return scriptType.compile(location);
    }

    private static String compressIfRequested(final Element element, final String code) throws IOException {
        if (code == null || code.isEmpty()) return code;
        return element.hasAttribute("compress") ? compressJavascriptCode(code) : code;
    }

}
