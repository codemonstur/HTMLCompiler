package htmlcompiler.tags.neko;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.file.Path;

public interface TagProcessor {
    TagProcessor NOOP = (file, document, element) -> false;

    boolean process(Path file, Document document, Element element) throws Exception;

}
