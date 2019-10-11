package htmlcompiler.compile.tags;

import htmlcompiler.compile.HtmlCompiler;
import htmlcompiler.model.MoveType;
import htmlcompiler.model.ScriptBag;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.model.ScriptType;
import htmlcompiler.tools.Logger;
import org.w3c.dom.Element;
import simplexml.SimpleXml;
import simplexml.utils.Interfaces.CheckedIterator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static htmlcompiler.compile.tags.TagProcessor.*;
import static htmlcompiler.compile.js.JsCompiler.compressJavascriptCode;
import static htmlcompiler.model.MoveType.storeCode;
import static htmlcompiler.model.MoveType.toMoveType;
import static htmlcompiler.model.ScriptType.detectScriptType;
import static htmlcompiler.model.ScriptType.javascript;
import static htmlcompiler.tools.HTML.*;
import static htmlcompiler.tools.IO.toLocation;
import static java.nio.charset.StandardCharsets.UTF_8;

public enum Script {;

    public static TagProcessor newScriptProcessor(final Logger log, final HtmlCompiler html, final SimpleXml xml, final ScriptBag scripts) {
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
                    return false;
                }
            }
            if (isHtml(element) && !isEmpty(element)) {
                final CheckedIterator<String> it = xml.iterateXml(new ByteArrayInputStream(element.getTextContent().getBytes(UTF_8)));
                final StringBuilder builder = new StringBuilder();
                while (it.hasNext()) {
                    builder.append(html.compileHtmlCode(file, it.next()));
                }
                element.setTextContent(builder.toString());
                return false;
            }
            if (element.hasAttribute("inline")) {
                final ScriptType type = detectScriptType(element, javascript);
                final File location = toLocation(file, element.getAttribute("src"), "script tag in %s has an invalid src location '%s'");
                element.setTextContent(compressIfRequested(element, type.compile(location)));
                removeAttributes(element, "inline", "compress", "src", "type");
                return false;
            }
            if (element.hasAttribute("src") && !element.hasAttribute("integrity") && !element.hasAttribute("no-security")) {
                addIntegrityAttributes(element, element.getAttribute("src"), file, html, log);
            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
            removeAttributes(element, "to-absolute", "no-security");
            return false;
        };
    }

    private static String compileScriptTag(final Element element, final ScriptType scriptType, final File parent) throws IOException, InvalidInput {
        if (!isEmpty(element)) return scriptType.compile(element.getTextContent(), parent);

        final File location = toLocation(parent, element.getAttribute("src"), "script tag in %s has an invalid src location '%s'");
        return scriptType.compile(location);
    }

    private static String compressIfRequested(final Element element, final String code) throws IOException {
        if (code == null || code.isEmpty()) return code;
        return element.hasAttribute("compress") ? compressJavascriptCode(code) : code;
    }

}
