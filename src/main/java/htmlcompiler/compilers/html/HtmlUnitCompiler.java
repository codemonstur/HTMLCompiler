package htmlcompiler.compilers.html;

import htmlcompiler.pojos.compile.ChecksConfig;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tools.Logger;
import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public final class HtmlUnitCompiler extends DefaultNekoCompiler {

    public HtmlUnitCompiler(final Logger log, final LibraryArchive archive, final ChecksConfig checksConfiguration) {
        super(log, archive, newCyberNekoParser());
    }

    private static DOMParser newCyberNekoParser() {
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
