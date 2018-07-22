package htmlcompiler.compiler;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import htmlcompiler.compiler.processors.*;
import htmlcompiler.logging.Logger;
import htmlcompiler.util.Loader;
import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import org.apache.maven.project.MavenProject;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static htmlcompiler.compiler.processors.Image.newImageProcessor;
import static htmlcompiler.compiler.processors.Import.newImportProcessor;
import static htmlcompiler.compiler.processors.Link.newLinkProcessor;
import static htmlcompiler.compiler.processors.Style.newStyleProcessor;
import static htmlcompiler.util.HTML.replaceTag;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static javax.xml.transform.OutputKeys.ENCODING;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static org.w3c.dom.Node.ELEMENT_NODE;

public final class HtmlCompiler {
    public static final String DOCTYPE = "<!DOCTYPE html>";

    private final DOMParser parser;
    private final HtmlCompressor compressor;
    private final Loader loader;
    private final PebbleEngine engine;
    private final boolean compress;

    private final Map<String, Object> context;
    private final Map<String, TagProcessor> processors = new HashMap<>();
    private final Map<String, Document> htmlBlocks = new HashMap<>();

    public HtmlCompiler(final Logger log, final Loader loader, final CssCompiler css, final JsCompiler javascript
            , final Map<String, Object> context) throws SAXNotRecognizedException, SAXNotSupportedException {
        this(log, loader, css, javascript, context, true);
    }
    public HtmlCompiler(final Logger log, final Loader loader, final CssCompiler css, final JsCompiler javascript
            , final Map<String, Object> context, final boolean compress) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.loader = requireNonNull(loader, "LinkLoader must not be null");
        this.context = context;
        this.compress = compress;

        this.parser = new DOMParser(new HTMLConfiguration());
        this.parser.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
        this.parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        this.parser.setFeature("http://cyberneko.org/html/features/document-fragment",true);

        this.engine = new PebbleEngine.Builder().build();

        this.compressor = new HtmlCompressor();
        this.compressor.setRemoveComments(true);
        this.compressor.setRemoveIntertagSpaces(true);

        processors.put("style", newStyleProcessor(css));
        processors.put("link", newLinkProcessor(this, log, css));
        processors.put("img", newImageProcessor(this));
        processors.put("script", new Script(this, log, javascript, new SimpleXml()));
        processors.put("import", newImportProcessor(this));
    }

    public static Map<String, Object> toTemplateContext(final MavenProject project) {
        final Map<String, Object> map = new HashMap<>();
        for (final Entry<Object, Object> entry : project.getProperties().entrySet()) {
            map.put(entry.getKey().toString(), entry.getValue());
        }
        for (final Entry<String, String> entry : System.getenv().entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public String compress(final String content) {
        return compressor.compress(content);
    }

    public String compile(final File file) throws Exception {
        final String output = toHtml(processHtml(file, htmlToDocument(loadTemplate(file.getAbsolutePath()))));
        return compress ? compress(output) : output;
    }
    public String compile(final File file, final String content) throws Exception {
        if (content == null || content.trim().isEmpty()) return "";
        final String output = toHtml(processHtml(file, htmlToDocument(content)));
        return compress ? compress(output) : output;
    }

    private String loadTemplate(final String resource) throws PebbleException, IOException {
        final PebbleTemplate template = engine.getTemplate(resource);
        try (final StringWriter writer = new StringWriter()) {
            template.evaluate(writer, context);
            return writer.toString();
        }
    }


    public Document processHtml(final File file, final Document document) throws Exception {
        if (document != null && document.getDocumentElement() != null)
            processElement(file, document, document.getDocumentElement());

        return document;
    }

    private static final TagProcessor NO_OP_PROCESSOR = (loader, file, document, element) -> false;

    private void processElement(final File file, final Document document, final Element node) throws Exception {
        final Document replaceCode = htmlBlocks.get(node.getTagName());
        if (replaceCode != null) {
            replaceTag(node, replaceCode.getDocumentElement());
            return;
        }
        processors.getOrDefault(node.getNodeName(), NO_OP_PROCESSOR).process(loader, file, document, node);

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == ELEMENT_NODE) {
                processElement(file, document, (Element) currentNode);
            }
        }
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

    public void addHtmlBlock(final String tag, final Document document) {
        htmlBlocks.put(tag, document);
    }

}
