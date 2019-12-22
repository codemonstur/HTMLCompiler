package htmlcompiler.checks.jsoup;

import org.jsoup.nodes.Element;

import java.io.File;

public interface TagChecker {

    public interface TailChecker extends TagChecker {
        @Override
        default void head(File file, Element element) {}
    }
    public interface HeadChecker extends TagChecker {
        @Override
        default void tail(File file, Element element) {}
    }

    void head(File file, Element element);
    void tail(File file, Element element);

}
