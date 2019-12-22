package htmlcompiler.tags.neko;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;

public interface TagProcessor {
    TagProcessor NOOP = (file, document, element) -> false;

    boolean process(File file, Document document, Element element) throws Exception;

}
