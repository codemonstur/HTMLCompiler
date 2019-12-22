package htmlcompiler.tags.jsoup;

import htmlcompiler.error.InvalidInput;
import htmlcompiler.error.UnrecognizedFileType;
import org.jsoup.nodes.Element;
import org.lesscss.LessException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface TagVisitor {

    TagVisitor NOOP = new TagVisitor() {
        public void head(File file, Element element, int depth) {}
        public void tail(File file, Element element, int depth) {}
    };

    public interface TailVisitor extends TagVisitor {
        @Override
        default void head(File file, Element element, int depth) {}
    }
    public interface HeadVisitor extends TagVisitor {
        @Override
        default void tail(File file, Element element, int depth) {}
    }

    void head(File file, Element element, int depth) throws IOException, InvalidInput, NoSuchAlgorithmException, LessException, UnrecognizedFileType;
    void tail(File file, Element element, int depth) throws IOException, InvalidInput, NoSuchAlgorithmException, LessException, UnrecognizedFileType;

}