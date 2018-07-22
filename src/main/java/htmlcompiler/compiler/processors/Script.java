package htmlcompiler.compiler.processors;

import htmlcompiler.compiler.HtmlCompiler;
import htmlcompiler.compiler.JsCompiler;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.logging.Logger;
import htmlcompiler.util.IO;
import htmlcompiler.util.Loader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import simplexml.SimpleXml;
import simplexml.utils.Interfaces.CheckedIterator;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static htmlcompiler.compiler.processors.TagProcessor.*;
import static htmlcompiler.util.HTML.*;
import static htmlcompiler.util.HTTP.isUrl;
import static htmlcompiler.util.HTTP.urlHasCorsAllowed;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

public final class Script implements TagProcessor {

    private final Logger log;
    private final HtmlCompiler html;
    private final JsCompiler javascript;
    private final SimpleXml xml;

    public Script(final HtmlCompiler html, final Logger log, final JsCompiler javascript, final SimpleXml xml) {
        this.html = requireNonNull(html, "HTML compiler must not be null");
        this.javascript = requireNonNull(javascript, "JavaScript compiler must not be null");
        this.log = requireNonNull(log, "Logger must not be null");
        this.xml = requireNonNull(xml, "SimpleXml must not be null");
    }

    @Override
    public boolean process(final Loader loader, final File file, final Document document, final Element element) throws Exception {
        final String code = element.getTextContent();
        if (isHtmlBlock(element)) {
            html.addHtmlBlock(element.getAttribute("tag"), html.htmlToDocument(element.getTextContent()));
            element.getParentNode().removeChild(element);
            return true;
        }
        if (isJavaScript(element) && notEmpty(code)) {
            element.setTextContent(javascript.compileJavaScript(code, file));
            return false;
        }
        if (isTypeScript(element) && notEmpty(code)) {
            element.setTextContent(javascript.compileTypeScript(code, file));
            element.setAttribute("type", "text/javascript");
            return false;
        }
        if (isHtml(element) && notEmpty(code)) {
            final CheckedIterator<String> it = xml.iterateXml(new ByteArrayInputStream(code.getBytes(UTF_8)));
            final StringBuilder builder = new StringBuilder();
            while (it.hasNext()) {
                builder.append(html.compile(file, it.next()));
            }
            element.setTextContent(builder.toString());
            return false;
        }
        if (element.hasAttribute("inline")) {
            return inlineScriptContent(element, loader, file, javascript);
        }
        if (element.hasAttribute("src") && !element.hasAttribute("integrity") && !element.hasAttribute("no-security")) {
            addIntegrityAttributes(element, loader, file, html, log);
        }
        if (element.hasAttribute("to-absolute")) {
            makeAbsolutePath(element, "src");
        }
        removeAttributes(element, "to-absolute", "no-security");
        return false;
    }

    private static boolean inlineScriptContent(final Element element, final Loader loader, final File file
            , final JsCompiler javascript) throws InvalidInput, IOException {
        final File location = loader.toLocation(file, element.getAttribute("src"), "script tag in %s has an invalid src location '%s'");

        String content = "";
        if (isJavaScript(element))
            content = javascript.compileJavaScript(IO.toString(location), location);
        else
        if (isTypeScript(element)) {
            content = javascript.compileTypeScript(IO.toString(location), location);
            element.setAttribute("type", "text/javascript");
        }
        if (element.hasAttribute("compress"))
            content = JsCompiler.compress(content);

        removeAttributes(element, "inline", "compress", "src");
        element.setTextContent(content);
        return false;
    }

    private static void addIntegrityAttributes(final Element element, final Loader loader, final File file
            , final HtmlCompiler html, final Logger log) throws IOException, NoSuchAlgorithmException, TransformerException {
        final String url = element.getAttribute("href");
        try {
            if (isUrl(url) && urlHasCorsAllowed(url)) {
                element.setAttribute("integrity", toIntegrityValue(loader.getAsBytes(url)));
                if (!element.hasAttribute("crossorigin")) element.setAttribute("crossorigin", "anonymous");
                log.warn(format("File %s has script tag without integrity, rewrote to: %s", loader.relative(file.getAbsolutePath()), html.toHtml(element)));
            }
        } catch (IOException e) {
            log.warn("Failed to get data for script src attribute " + url + ", error is " + e.getMessage());
            throw e;
        }
    }

    private static boolean isHtmlBlock(final Element element) {
        return element.hasAttribute("type") && "text/htmlblock".equalsIgnoreCase(element.getAttribute("type"));
    }
}
