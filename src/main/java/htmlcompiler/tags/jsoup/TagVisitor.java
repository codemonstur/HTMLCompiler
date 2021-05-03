package htmlcompiler.tags.jsoup;

import htmlcompiler.pojos.compile.Config;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.error.UnrecognizedFileType;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public interface TagVisitor {

    TagVisitor NOOP = new TagVisitor() {
        public void head(Config config, Path file, Element element, int depth) {}
        public void tail(Config config, Path file, Element element, int depth) {}
    };

    public interface TailVisitor extends TagVisitor {
        @Override
        default void head(Config config, Path file, Element element, int depth) {}
    }
    public interface HeadVisitor extends TagVisitor {
        @Override
        default void tail(Config config, Path file, Element element, int depth) {}
    }

    void head(Config config, Path file, Element element, int depth) throws IOException, InvalidInput, NoSuchAlgorithmException, UnrecognizedFileType;
    void tail(Config config, Path file, Element element, int depth) throws Exception;

}