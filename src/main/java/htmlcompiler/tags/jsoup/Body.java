package htmlcompiler.tags.jsoup;

import htmlcompiler.model.ScriptBag;
import org.jsoup.nodes.Element;

import java.io.File;

public enum Body {;

    public static TagVisitor newBodyVisitor(final ScriptBag scripts) {
        return new TagVisitor() {
            public void head(File file, Element node, int depth) {
                final String startCode = scripts.getScriptAtBodyStart().trim();
                if (!startCode.isEmpty()) {
                    final Element scriptStart = node.ownerDocument().createElement("script");
                    scriptStart.text(startCode);
                    node.childNodes().add(0, scriptStart);
                }
            }
            public void tail(File file, Element node, int depth) {
                final String code = scripts.getScriptAtBodyEnd().trim();
                if (!code.isEmpty()) {
                    final Element script = node.ownerDocument().createElement("script");
                    script.text(code);
                    node.childNodes().add(script);
                }
            }
        };
    }

}
