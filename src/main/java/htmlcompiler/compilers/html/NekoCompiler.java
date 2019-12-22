package htmlcompiler.compilers.html;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public interface NekoCompiler extends HtmlCompiler {

    Document processHtml(final File file, final Document document) throws Exception;
    Document htmlToDocument(final String html) throws IOException, SAXException;
    String toHtml(final Node node) throws TransformerException, IOException;

}
