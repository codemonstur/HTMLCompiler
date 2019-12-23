package tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static util.Concurrent.executeMultiThreaded;

public class HarvestTagsAndAttributes {

    public static void main(final String... args) throws IOException {
        final Set<String> tags = new HashSet<>();
        final Set<String> attributes = new HashSet<>();

//        harvestHtmlFromUri(URI.create("http://cisco.com/"), tags, attributes);

        executeMultiThreaded(100, loadUris(),
            task -> () -> harvestHtmlFromUri(task, tags, attributes));

        System.out.println("Tags: " + tags);
        System.out.println("Attributes: " + attributes);
    }

    private static Set<URI> loadUris() throws IOException {
        final Path urisFile = Path.of("htmlcompiler/src/test/resources/uris.txt");
        return Files.lines(urisFile)
            .map(HarvestTagsAndAttributes::toURL)
            .collect(toSet());
    }

    private static URI toURL(final String url) {
        try {
            return new URI("http://"+url+"/");
        } catch (Exception e) {
            return null;
        }
    }

    private static void harvestHtmlFromUri(final URI uri, final Set<String> tags, final Set<String> attributes) {
        if (uri == null) return;

        try {
            final Document parse = Jsoup.parse(uri.toURL(), 5000);
            parse.traverse(new NodeVisitor() {
                public void head(Node node, int depth) {
                    if (node instanceof Element) {
                        final Element element = (Element) node;
                        synchronized (tags) {
                            tags.add(element.tagName().toLowerCase());
                        }
                        synchronized (attributes) {
                            for (final var attribute : element.attributes()) {
                                attributes.add(attribute.getKey().toLowerCase());
                            }
                        }
                    }
                }
                public void tail(Node node, int depth) {}
            });
        } catch (Exception e) {
//            System.err.println("An error occurred for " + uri + ": " + e.getMessage());
        }
    }

}
