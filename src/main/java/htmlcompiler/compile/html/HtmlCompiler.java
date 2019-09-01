package htmlcompiler.compile.html;

import com.google.gson.Gson;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import htmlcompiler.model.ScriptBag;
import htmlcompiler.tools.IO;
import htmlcompiler.tools.Logger;
import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import simplexml.SimpleXml;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static htmlcompiler.compile.html.Body.newBodyProcessor;
import static htmlcompiler.compile.html.Head.newHeadProcessor;
import static htmlcompiler.compile.html.Image.newImageProcessor;
import static htmlcompiler.compile.html.Import.newImportProcessor;
import static htmlcompiler.compile.html.Include.newIncludeProcessor;
import static htmlcompiler.compile.html.Library.newLibraryProcessor;
import static htmlcompiler.compile.html.Link.newLinkProcessor;
import static htmlcompiler.compile.html.Script.newScriptProcessor;
import static htmlcompiler.compile.html.Style.newStyleProcessor;
import static htmlcompiler.compile.html.TagProcessor.NOOP;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.xml.transform.OutputKeys.ENCODING;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static org.w3c.dom.Node.ELEMENT_NODE;

public final class HtmlCompiler {

    private final File inputDir;
    private final DOMParser parser;
    private final HtmlCompressor compressor;
    private final Map<String, TagProcessor> processors;

    public HtmlCompiler(final Logger log, final File inputDir) throws MojoFailureException {
        try {
            this.inputDir = inputDir;
            this.parser = newDefaultDomParser();
            this.compressor = newDefaultHtmlCompressor();
            this.processors = newDefaultTagProcessors(log, this, new Gson());
        } catch (SAXNotRecognizedException | SAXNotSupportedException | IOException e) {
            throw new MojoFailureException("Initialization error", e);
        }
    }

    private static DOMParser newDefaultDomParser() throws SAXNotRecognizedException, SAXNotSupportedException {
        final DOMParser parser = new DOMParser(new HTMLConfiguration());
        parser.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        parser.setFeature("http://cyberneko.org/html/features/document-fragment",true);
        return parser;
    }
    private static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }
    private static Map<String, TagProcessor> newDefaultTagProcessors(final Logger log, final HtmlCompiler html, final Gson gson) throws IOException {
        final ScriptBag scripts = new ScriptBag();
        final Map<String, TagProcessor> processors = new HashMap<>();
        processors.put("style", newStyleProcessor());
        processors.put("link", newLinkProcessor(html, log));
        processors.put("img", newImageProcessor(html));
        processors.put("script", newScriptProcessor(log, html, new SimpleXml(), scripts));
        processors.put("body", newBodyProcessor(scripts));
        processors.put("head", newHeadProcessor(scripts));
        processors.put("import", newImportProcessor(html));
        processors.put("include", newIncludeProcessor(html));
        processors.put("library", newLibraryProcessor(gson));
        return processors;
    }

    public String compressHtmlCode(final String content) {
        return compressor.compress(content);
    }
    public String compressHtmlFile(final File input) throws IOException {
        return compressor.compress(IO.toString(input));
    }
    public String compressHtmlFile(final String filename) throws IOException {
        return compressor.compress(IO.toString(new File(inputDir, filename)));
    }

    public String compileHtmlFile(final File file) throws Exception {
        return compressHtmlCode(toHtml(processHtml(file, htmlToDocument(Files.readString(file.toPath())))));
    }
    public String compileHtmlCode(final File file, final String content) throws Exception {
        if (content == null || content.trim().isEmpty()) return "";
        return toHtml(processHtml(file, htmlToDocument(content)));
    }

    public Document processHtml(final File file, final Document document) throws Exception {
        if (document != null && document.getDocumentElement() != null)
            processElement(inputDir, file, document, document.getDocumentElement());

        return document;
    }

    private void processElement(final File inputDir, final File file, final Document document, final Element node) throws Exception {
        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == ELEMENT_NODE) {
                processElement(inputDir, file, document, (Element) currentNode);
            }
        }

        processors.getOrDefault(node.getNodeName(), NOOP).process(inputDir, file, document, node);
    }

    public Document htmlToDocument(final String html) throws IOException, SAXException {
        parser.parse(new InputSource(new StringReader(html)));
        return parser.getDocument();
    }

    public String toHtml(final Node node) throws TransformerException, IOException {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(ENCODING, UTF_8.toString());
        transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");

        try (final StringWriter writer = new StringWriter()) {
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        }
    }

}
