package htmlcompiler.compilers.tags;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.compilers.tags.TagVisitor.TailVisitor;
import htmlcompiler.pojos.error.InvalidInput;
import org.jsoup.nodes.Node;

import java.nio.file.Files;
import java.nio.file.Path;

import static htmlcompiler.compilers.tags.TagParsing.replaceWith;
import static htmlcompiler.utils.IO.toLocation;

public enum Import {;

    public static TagVisitor newImportVisitor(final HtmlCompiler compiler) {
        return (TailVisitor) (config, file, node, depth) -> {
            final Path include = toSourceLocation(node, "src", file);
            final String content = Files.readString(include);
            if (content.isEmpty()) node.remove();
            else replaceWith(node, compiler.compileHtmlFragment(include, content).children());
        };
    }

    private static Path toSourceLocation(final Node node, final String attribute, final Path file) throws InvalidInput {
        if (!node.hasAttr(attribute)) throw new InvalidInput(String.format("<import> is missing '%s' attribute", attribute));
        return toLocation(file.getParent(), node.attr(attribute), "<import> in %s has an invalid src location '%s'");
    }

}
