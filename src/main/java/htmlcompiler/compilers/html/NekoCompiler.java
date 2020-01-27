package htmlcompiler.compilers.html;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;

public interface NekoCompiler extends HtmlCompiler {

    Document processHtml(final Path file, final Document document) throws Exception;
    Document htmlToDocument(final String html) throws IOException, SAXException;
    String toHtml(final Node node) throws TransformerException, IOException;

}
