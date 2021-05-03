package htmlcompiler.tags.jsoup;

import htmlcompiler.pojos.compile.Config;
import htmlcompiler.pojos.compile.ScriptBag;
import org.jsoup.nodes.Element;

import java.nio.file.Path;

public enum Head {;

    public static TagVisitor newHeadVisitor(final ScriptBag scripts) {
        return new TagVisitor() {
            public void head(Config config, Path file, Element node, int depth) {
                final String startCode = scripts.getScriptAtHeadStart().trim();
                if (!startCode.isEmpty()) {
                    final Element scriptStart = node.ownerDocument().createElement("script");
                    scriptStart.text(startCode);
                    node.childNodes().add(0, scriptStart);
                }
            }
            public void tail(Config config, Path file, Element node, int depth) {
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
