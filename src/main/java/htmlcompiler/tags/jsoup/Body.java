package htmlcompiler.tags.jsoup;

import htmlcompiler.pojos.compile.ChecksConfig;
import htmlcompiler.pojos.compile.ScriptBag;
import org.jsoup.nodes.Element;

import java.nio.file.Path;

public enum Body {;

    public static TagVisitor newBodyVisitor(final ScriptBag scripts) {
        return new TagVisitor() {
            public void head(ChecksConfig config, Path file, Element node, int depth) {
                final String startCode = scripts.getScriptAtBodyStart().trim();
                if (!startCode.isEmpty()) {
                    final Element scriptStart = node.ownerDocument().createElement("script");
                    scriptStart.text(startCode);
                    node.prependChild(scriptStart);
                }
            }
            public void tail(ChecksConfig config, Path file, Element node, int depth) {
                final String code = scripts.getScriptAtBodyEnd().trim();
                if (!code.isEmpty()) {
                    final Element script = node.ownerDocument().createElement("script");
                    script.text(code);
                    node.appendChild(script);
                }
            }
        };
    }

}
