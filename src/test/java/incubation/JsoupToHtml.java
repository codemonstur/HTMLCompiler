package incubation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsoupToHtml {

    public static void main(final String... args) throws IOException {

        final File file = new File("htmlcompiler/src/test/resources/merging/test3.html");
        final String content = Files.readString(file.toPath().toAbsolutePath());

        final Document document = removeDoctype(Jsoup.parse(content));
        document.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                System.out.println(node.nodeName());
            }

            @Override
            public void tail(Node node, int depth) {

            }
        });
        System.out.println("<!DOCTYPE html>"+document.html());
    }

    private static Document removeDoctype(final Document document) {
        final Node node = document.childNode(0);
        if ("#doctype".equals(node.nodeName())) {
            node.remove();
        }
        return document;
    }

}
