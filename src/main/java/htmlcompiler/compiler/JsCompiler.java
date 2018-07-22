package htmlcompiler.compiler;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import htmlcompiler.error.UnrecognizedFileType;
import htmlcompiler.util.Coding;
import htmlcompiler.util.Loader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static htmlcompiler.util.IO.relativize;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public final class JsCompiler {

    private final Loader loader;
    private final boolean compress;
    public JsCompiler(final Loader loader) {
        this(loader, true);
    }
    public JsCompiler(final Loader loader, final boolean compress) {
        this.loader = requireNonNull(loader, "Loader must not be null");
        this.compress = compress;
    }

    public enum Type {
        MINIFIED_JAVASCRIPT, JAVASCRIPT, TYPESCRIPT
    }

    private static Type toType(final String location) throws UnrecognizedFileType {
        if (location.endsWith(".min.js")) return Type.MINIFIED_JAVASCRIPT;
        if (location.endsWith(".js")) return Type.JAVASCRIPT;
        if (location.endsWith(".ts")) return Type.TYPESCRIPT;
        throw new UnrecognizedFileType(location);
    }

    public static String compress(final String content) throws IOException {
        final JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(content), null);
        final StringWriter writer = new StringWriter();
        compressor.compress(writer, -1, true, false, false, false);
        return writer.toString();
    }

    public String compile(final File location) throws UnrecognizedFileType, IOException {
        return compile(toType(location.getName()), location, compress);
    }
    public String compile(final Type type, final File location) throws UnrecognizedFileType, IOException {
        return compile(type, location, compress);
    }
    public String compile(final Type type, final File location, final boolean compress) throws IOException, UnrecognizedFileType {
        final String output = compile(type, location, loader.getAsString(location.getAbsolutePath()));
        return compress ? compress(output) : output;
    }
    public String compile(final Type type, final File location, final String content) throws IOException, UnrecognizedFileType {
        String output;
        switch (type) {
            case MINIFIED_JAVASCRIPT: return content;
            case JAVASCRIPT: output = compileJavaScript(content, location); break;
            case TYPESCRIPT: output = compileTypeScript(content, location); break;
            default: throw new UnrecognizedFileType("");
        }
        return compress ? compress(output) : output;
    }

    public String compileTypeScript(final String content, final File location) throws IOException {
        final File tempFile = File.createTempFile("tsc_", ".tmp");
        try {
            Process p = Runtime.getRuntime()
                    .exec("tsc --outFile " + tempFile.getAbsolutePath() + " " + location.getAbsolutePath());
            p.waitFor();
            return compress(Coding.toString(tempFile));
        } catch (InterruptedException e) {
            throw new IOException("Couldn't wait for tsc", e);
        } finally {
            tempFile.delete();
        }
    }

    public String compileJavaScript(final String content, final File location) throws IOException {
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

    private List<Node> resolveIncludeStatement(final List<Node> nodes, final Set<Path> alreadyLoaded) throws IOException {
        final List<Node> ret = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) instanceof TextNode) {
                ret.add(nodes.get(i));
                continue;
            }

            final ImportNode node = (ImportNode) nodes.get(i);
            if (alreadyLoaded.contains(node.path)) continue;

            try {
                final String content = loader.getAsString(relativize(loader.getRoot(), node.path.toString()));
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

    private interface Node {}
    private static class TextNode implements Node {
        private final String text;

        private TextNode(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
    private static class ImportNode implements Node {
        private final Integer lineNum;
        private final Path source;
        private final Path path;

        private ImportNode(final Integer lineNum, final Path source, final Path path) {
            this.lineNum = lineNum;
            this.source = source;
            this.path = path;
        }

        @Override
        public String toString() {
            return path.toString();
        }
    }

}
