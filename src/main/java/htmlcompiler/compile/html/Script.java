package htmlcompiler.compile.html;

import htmlcompiler.model.MoveType;
import htmlcompiler.model.ScriptBag;
import htmlcompiler.model.error.InvalidInput;
import htmlcompiler.model.error.UnrecognizedFileType;
import htmlcompiler.tools.Logger;
import org.w3c.dom.Element;
import simplexml.SimpleXml;
import simplexml.utils.Interfaces.CheckedIterator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static htmlcompiler.compile.html.TagProcessor.*;
import static htmlcompiler.compile.js.JsCompiler.compileJavascriptFile;
import static htmlcompiler.compile.js.JsCompiler.compressJavascriptCode;
import static htmlcompiler.compile.js.TypeScriptCompiler.compileTypeScript;
import static htmlcompiler.model.MoveType.storeCode;
import static htmlcompiler.model.MoveType.toMoveType;
import static htmlcompiler.tools.HTML.*;
import static htmlcompiler.tools.IO.toLocation;
import static java.nio.charset.StandardCharsets.UTF_8;

public enum Script {;

    public static TagProcessor newScriptProcessor(final Logger log, final HtmlCompiler html, final SimpleXml xml, final ScriptBag scripts) {
        return (inputDir, file, document, element) -> {
            final String code = element.getTextContent();
            if (!element.hasAttribute("src") && isEmpty(code)) {
                deleteTag(element);
                return true;
            }

            if (element.hasAttribute("move")) {
                final MoveType type = toMoveType(element.getAttribute("move"), null);
                storeCode(extractJavascriptCode(element, file), type, scripts);
                deleteTag(element);
                return true;
            }

            if (isJavaScript(element) && notEmpty(code)) {
                element.setTextContent(compressJavascriptCode(code));
                removeAttributes(element, "compress");
                return false;
            }
            if (isTypeScript(element) && notEmpty(code)) {
                element.setTextContent(compileTypeScript(file, code));
                return false;
            }
            if (isHtml(element) && notEmpty(code)) {
                final CheckedIterator<String> it = xml.iterateXml(new ByteArrayInputStream(code.getBytes(UTF_8)));
                final StringBuilder builder = new StringBuilder();
                while (it.hasNext()) {
                    builder.append(html.compileHtmlCode(file, it.next()));
                }
                element.setTextContent(builder.toString());
                return false;
            }
            if (element.hasAttribute("inline")) {
                element.setTextContent(extractJavascriptCode(element, file));
                removeAttributes(element, "inline", "compress", "src", "type");
                return false;
            }
            if (element.hasAttribute("src") && !element.hasAttribute("integrity") && !element.hasAttribute("no-security")) {
                addIntegrityAttributes(element, element.getAttribute("src"), inputDir, file, html, log);
            }
            if (element.hasAttribute("to-absolute")) {
                makeAbsolutePath(element, "src");
            }
            removeAttributes(element, "to-absolute", "no-security");
            return false;
        };
    }

    private static String extractJavascriptCode(final Element element, final File file) throws IOException, UnrecognizedFileType, InvalidInput {
        final File location = toLocation(file, element.getAttribute("src"), "script tag in %s has an invalid src location '%s'");

        String content = "";
        if (isJavaScript(element))
            content = compileJavascriptFile(location);
        else if (isTypeScript(element)) {
            content = compileTypeScript(location);
        }

        if (element.hasAttribute("compress"))
            content = compressJavascriptCode(content);
        return content;
    }

}
