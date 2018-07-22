package htmlcompiler.compile.js;

import htmlcompiler.model.extjs.ImportNode;
import htmlcompiler.model.extjs.Node;
import htmlcompiler.model.extjs.TextNode;
import htmlcompiler.tools.IO;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

public enum ExtendedJSCompiler {;

    public static String compileExtendedJavaScript(final String content, final File location) throws IOException {
        final Set<Path> alreadyLoaded = new HashSet<>();

        if (location == null || location.toString().isEmpty())
            throw new IllegalArgumentException(format("%s(%d): Invalid include %s", location, 0, location));
        if (content == null)
            throw new IllegalArgumentException(format("%s(%d): Missing include %s", location, 0, location));

        List<Node> nodes = loadJs(location.toPath(), content, alreadyLoaded);
        while (hasImports(nodes)) nodes = resolveIncludeStatement(nodes, alreadyLoaded);
        return concatenate(nodes);
    }

    private static boolean hasImports(final List<Node> nodes) {
        for (final Node node : nodes)
            if (node instanceof ImportNode)
                return true;
        return false;
    }

    private static List<Node> resolveIncludeStatement(final List<Node> nodes, final Set<Path> alreadyLoaded) throws IOException {
        final List<Node> ret = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) instanceof TextNode) {
                ret.add(nodes.get(i));
                continue;
            }

            final ImportNode node = (ImportNode) nodes.get(i);
            if (alreadyLoaded.contains(node.path)) continue;

            try {
                final String content = IO.toString(node.path.toFile());
                ret.addAll(loadJs(node.path, content, alreadyLoaded));
                for (int j = i+1; j < nodes.size(); j++) {
                    ret.add(nodes.get(j));
                }
                alreadyLoaded.add(node.path);
                return ret;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(format("%s(%d): Missing include %s", node.source, node.lineNum, node.path));
            }
        }
        return ret;
    }

    private static String concatenate(final List<Node> nodes) {
        final StringBuilder builder = new StringBuilder();
        for (Node node : nodes) builder.append(node.toString());
        return builder.toString();
    }

    private static List<Node> loadJs(final Path sourceLocation, final String content, final Set<Path> loaded) throws IOException {
        final List<Node> nodes = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        try (final BufferedReader in = new BufferedReader(new StringReader(content))) {
            String line; Path file; int i = 0;
            while ((line = in.readLine()) != null) {
                i++;
                if (line.startsWith("include")) {
                    if (line.length() < 9) continue;

                    file = resolveInclude(sourceLocation, line.substring(8));
                    if (loaded.contains(file)) continue;
                    if (builder.length() > 0) nodes.add(new TextNode(builder.toString()));
                    nodes.add(new ImportNode(i, sourceLocation, file));
                    builder = new StringBuilder();
                } else {
                    builder.append(line);
                    builder.append("\n");
                }
            }
        }
        nodes.add(new TextNode(builder.toString()));

        return nodes;
    }

    private static Path resolveInclude(final Path src, final String dst) {
        return toDirname(src).resolve(dst).normalize();
    }
    private static Path toDirname(final Path path) {
        final Path parent = path.getParent();
        return (parent != null) ? parent : path.getRoot();
    }


}
