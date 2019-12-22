package htmlcompiler.tags.jsoup;

import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.tags.jsoup.TagVisitor.TailVisitor;
import htmlcompiler.error.InvalidInput;
import htmlcompiler.tools.IO;
import org.jsoup.nodes.Node;

import java.io.File;

import static htmlcompiler.tags.jsoup.TagParsingJsoup.replaceWith;
import static htmlcompiler.tools.IO.toLocation;

public enum Include {;

    public static TagVisitor newIncludeVisitor(final JsoupCompiler compiler) {
        return (TailVisitor) (file, node, depth) -> {
            final File include = toSourceLocation(node, "src", file);
            final String content = IO.toString(include);
            if (content.isEmpty()) node.remove();
            else replaceWith(node, compiler.compileHtmlFragment(include, content).children());
        };
    }

    private static File toSourceLocation(final Node node, final String attribute, final File file) throws InvalidInput {
        if (!node.hasAttr(attribute)) throw new InvalidInput(String.format("<include> is missing '%s' attribute", attribute));
        return toLocation(file.getParentFile(), node.attr(attribute), "<include> in %s has an invalid src location '%s'");
    }

}