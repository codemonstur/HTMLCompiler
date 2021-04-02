package htmlcompiler.tags.jsoup;

import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.pojos.error.InvalidInput;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import org.jsoup.nodes.Node;

import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.tags.jsoup.TagParsingJsoup.replaceWith;
import static htmlcompiler.tools.IO.toLocation;

public enum Include {;

    public static TagVisitor newIncludeVisitor(final JsoupCompiler compiler) {
        return (TailVisitor) (config, file, node, depth) -> {
            final Path include = toSourceLocation(node, "src", file);
            final String content = Files.readString(include);
            if (content.isEmpty()) node.remove();
            else replaceWith(node, compiler.compileHtmlFragment(include, content).children());
        };
    }

    private static Path toSourceLocation(final Node node, final String attribute, final Path file) throws InvalidInput {
        if (!node.hasAttr(attribute)) throw new InvalidInput(String.format("<include> is missing '%s' attribute", attribute));
        return toLocation(file.getParent(), node.attr(attribute), "<include> in %s has an invalid src location '%s'");
    }

}
