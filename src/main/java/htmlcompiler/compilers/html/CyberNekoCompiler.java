package htmlcompiler.compilers.html;

import htmlcompiler.tools.Logger;
import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.io.IOException;

public final class CyberNekoCompiler extends DefaultNekoCompiler {

    public CyberNekoCompiler(final Logger log) throws IOException {
        super(log, newCyberNekoParser());
    }

    private static DOMParser newCyberNekoParser() throws IOException {
        try {
            final DOMParser parser = new DOMParser(new HTMLConfiguration());
            parser.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            parser.setFeature("http://cyberneko.org/html/features/document-fragment",true);
            return parser;
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new IOException("Initialization error", e);
        }
    }

}
