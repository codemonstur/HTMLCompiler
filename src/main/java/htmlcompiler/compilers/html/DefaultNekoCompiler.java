package htmlcompiler.compilers.html;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.pojos.compile.ScriptBag;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tags.neko.TagProcessor;
import htmlcompiler.tools.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xmlparser.XmlParser;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static htmlcompiler.compilers.html.HtmlCompiler.newDefaultHtmlCompressor;
import static htmlcompiler.tags.neko.Body.newBodyProcessor;
import static htmlcompiler.tags.neko.Head.newHeadProcessor;
import static htmlcompiler.tags.neko.Image.newImageProcessor;
import static htmlcompiler.tags.neko.Import.newImportProcessor;
import static htmlcompiler.tags.neko.Include.newIncludeProcessor;
import static htmlcompiler.tags.neko.Library.newLibraryProcessor;
import static htmlcompiler.tags.neko.Link.newLinkProcessor;
import static htmlcompiler.tags.neko.Meta.newMetaProcessor;
import static htmlcompiler.tags.neko.Script.newScriptProcessor;
import static htmlcompiler.tags.neko.Style.newStyleProcessor;
import static htmlcompiler.tags.neko.TagProcessor.NOOP;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.xml.transform.OutputKeys.ENCODING;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static org.w3c.dom.Node.ELEMENT_NODE;

public abstract class DefaultNekoCompiler implements NekoCompiler {

    private final DOMParser parser;
    private final HtmlCompressor compressor;
    private final Map<String, TagProcessor> processors;

    public DefaultNekoCompiler(final Logger log, final LibraryArchive archive, final DOMParser parser) {
        this.parser = parser;
        this.compressor = newDefaultHtmlCompressor();
        this.processors = newDefaultTagProcessors(log, this, archive);
    }

    private static Map<String, TagProcessor> newDefaultTagProcessors(final Logger log, final NekoCompiler html, final LibraryArchive archive) {
        final ScriptBag scripts = new ScriptBag();
        final Map<String, TagProcessor> processors = new HashMap<>();
        processors.put("style", newStyleProcessor());
        processors.put("link", newLinkProcessor(html, log));
        processors.put("img", newImageProcessor(html));
        processors.put("script", newScriptProcessor(log, html, new XmlParser(), scripts));
        processors.put("body", newBodyProcessor(scripts));
        processors.put("head", newHeadProcessor(scripts));
        processors.put("import", newImportProcessor(html));
        processors.put("include", newIncludeProcessor(html));
        processors.put("library", newLibraryProcessor(archive));
        processors.put("meta", newMetaProcessor(archive));
        return processors;
    }

    public String doctypeCompressCompile(final Path file, final String content) throws InvalidInput {
        return "<!DOCTYPE html>"+compressHtmlCode(compileHtmlCode(file, content));
    }

    public String compressHtmlCode(final String content) {
        return compressor.compress(content);
    }

    public String compileHtmlCode(final Path file, final String content) throws InvalidInput {
        if (content == null || content.trim().isEmpty()) return "";
        try {
            return toHtml(processHtml(file, htmlToDocument(content)));
        } catch (Exception e) {
            throw new InvalidInput(e);
        }
    }

    public Document processHtml(final Path file, final Document document) throws Exception {
        if (document != null && document.getDocumentElement() != null)
            processElement(file, document, document.getDocumentElement());

        return document;
    }

    private void processElement(final Path file, final Document document, final Element node) throws Exception {
        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == ELEMENT_NODE) {
                processElement(file, document, (Element) currentNode);
            }
        }

        processors.getOrDefault(node.getNodeName(), NOOP).process(file, document, node);
    }

    public Document htmlToDocument(final String html) throws IOException, SAXException {
        parser.parse(new InputSource(new StringReader(html)));
        return parser.getDocument();
    }

    public String toHtml(final Node node) throws TransformerException, IOException {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(ENCODING, UTF_8.toString());
        transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xalan}omit-meta-tag", "yes");

        try (final StringWriter writer = new StringWriter()) {
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        }
    }

}
