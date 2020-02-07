package htmlcompiler.tags.jsoup;

import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.error.UnrecognizedFileType;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public interface TagVisitor {

    TagVisitor NOOP = new TagVisitor() {
        public void head(Path file, Element element, int depth) {}
        public void tail(Path file, Element element, int depth) {}
    };

    public interface TailVisitor extends TagVisitor {
        @Override
        default void head(Path file, Element element, int depth) {}
    }
    public interface HeadVisitor extends TagVisitor {
        @Override
        default void tail(Path file, Element element, int depth) {}
    }

    void head(Path file, Element element, int depth) throws IOException, InvalidInput, NoSuchAlgorithmException, UnrecognizedFileType;
    void tail(Path file, Element element, int depth) throws Exception;

}