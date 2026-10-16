package htmlcompiler.compilers.tags;

import htmlcompiler.compilers.HtmlCompiler;
import htmlcompiler.pojos.compile.CompilerConfig;
import org.jsoup.nodes.Element;

import java.nio.file.Path;

public enum Head {;

    public static TagVisitor newHeadVisitor(final HtmlCompiler html) {
        return new TagVisitor() {
            public void head(final CompilerConfig config, final Path file, final Element node, final int depth) {
                final String startCode = html.scripts.getScriptAtHeadStart().trim();
                if (!startCode.isEmpty()) {
                    final Element scriptStart = node.ownerDocument().createElement("script");
                    scriptStart.text(startCode);
                    node.prependChild(scriptStart);
                }
            }
            public void tail(final CompilerConfig config, final Path file, final Element node, final int depth) {
                final String code = html.scripts.getScriptAtHeadEnd().trim();
                if (!code.isEmpty()) {
                    final Element script = node.ownerDocument().createElement("script");
                    script.text(code);
                    node.appendChild(script);
                }
            }
        };
    }

}
