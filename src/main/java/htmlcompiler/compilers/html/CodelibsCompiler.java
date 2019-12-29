package htmlcompiler.compilers.html;

import htmlcompiler.library.LibraryArchive;
import htmlcompiler.tools.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.codelibs.nekohtml.HTMLConfiguration;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.util.Map;

public final class CodelibsCompiler extends DefaultNekoCompiler {

    public CodelibsCompiler(final Logger log, final LibraryArchive archive, final Map<String, Boolean> checksConfiguration) {
        super(log, archive, newCodelibsParser());
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

}
