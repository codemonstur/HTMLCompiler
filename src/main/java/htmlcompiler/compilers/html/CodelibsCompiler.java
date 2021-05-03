package htmlcompiler.compilers.html;

import htmlcompiler.pojos.compile.Config;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tools.Logger;
import org.codelibs.nekohtml.HTMLConfiguration;
import org.codelibs.xerces.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.io.IOException;
import java.io.StringReader;

public final class CodelibsCompiler extends DefaultNekoCompiler {

    private final DOMParser parser;

    public CodelibsCompiler(final Logger log, final LibraryArchive archive, final Config configuration) {
        super(log, archive);
        this.parser = newCodelibsParser();
    }

    private static DOMParser newCodelibsParser() {
        try {
            final DOMParser parser = new DOMParser(new HTMLConfiguration());
            parser.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            parser.setFeature("http://cyberneko.org/html/features/document-fragment",true);
            return parser;
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new IllegalStateException("Initialization error", e);
        }
    }

    @Override
    public Document htmlToDocument(final String html) throws IOException, SAXException {
        parser.parse(new InputSource(new StringReader(html)));
        return parser.getDocument();
    }

}
