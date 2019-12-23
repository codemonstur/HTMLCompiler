package htmlcompiler.tags.jsoup;

import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import htmlcompiler.tools.IO;
import org.jsoup.nodes.Node;

import java.io.File;

import static htmlcompiler.tools.IO.toLocation;

public enum Import {;

    public static TagVisitor newImportVisitor(final JsoupCompiler compiler) {
        return (TailVisitor) (file, node, depth) -> {
            final File include = toSourceLocation(node, "src", file);
            final String content = IO.toString(include);
            if (content.isEmpty()) node.remove();
            else TagParsingJsoup.replaceWith(node, compiler.compileHtmlFragment(include, content).children());
        };
    }

    private static File toSourceLocation(final Node node, final String attribute, final File file) throws InvalidInput {
        if (!node.hasAttr(attribute)) throw new InvalidInput(String.format("<import> is missing '%s' attribute", attribute));
        return toLocation(file.getParentFile(), node.attr(attribute), "<import> in %s has an invalid src location '%s'");
    }

}
