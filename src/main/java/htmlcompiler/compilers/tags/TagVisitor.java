package htmlcompiler.compilers.tags;

import htmlcompiler.pojos.compile.CompilerConfig;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.pojos.error.UnrecognizedFileType;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public interface TagVisitor {

    TagVisitor NOOP = new TagVisitor() {
        public void head(CompilerConfig config, Path file, Element element, int depth) {}
        public void tail(CompilerConfig config, Path file, Element element, int depth) {}
    };

    public interface TailVisitor extends TagVisitor {
        @Override
        default void head(CompilerConfig config, Path file, Element element, int depth) {}
    }
    public interface HeadVisitor extends TagVisitor {
        @Override
        default void tail(CompilerConfig config, Path file, Element element, int depth) {}
    }

    void head(CompilerConfig config, Path file, Element element, int depth) throws IOException, InvalidInput, NoSuchAlgorithmException, UnrecognizedFileType;
    void tail(CompilerConfig config, Path file, Element element, int depth) throws Exception;

}