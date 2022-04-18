package htmlcompiler.compilers.tags;

import htmlcompiler.pojos.compile.CompilerConfig;
import htmlcompiler.pojos.compile.ScriptBag;
import org.jsoup.nodes.Element;

import java.nio.file.Path;

public enum Head {;

    public static TagVisitor newHeadVisitor(final ScriptBag scripts) {
        return new TagVisitor() {
            public void head(final CompilerConfig config, final Path file, final Element node, final int depth) {
                final String startCode = scripts.getScriptAtHeadStart().trim();
                if (!startCode.isEmpty()) {
                    final Element scriptStart = node.ownerDocument().createElement("script");
                    scriptStart.text(startCode);
                    node.childNodes().add(0, scriptStart);
                }
            }
            public void tail(final CompilerConfig config, final Path file, final Element node, final int depth) {
                final String code = scripts.getScriptAtHeadEnd().trim();
                if (!code.isEmpty()) {
                    final Element script = node.ownerDocument().createElement("script");
                    script.text(code);
                    node.childNodes().add(script);
                }
            }
        };
    }

}
