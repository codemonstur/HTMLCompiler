package htmlcompiler.compile.html;

import htmlcompiler.model.ScriptBag;
import org.w3c.dom.Node;

public enum Body {;

    public static TagProcessor newBodyProcessor(final HtmlCompiler html, final ScriptBag scripts) {
        return (inputDir, file, document, element) -> {
            final Node scriptStart = document.createElement("script");
            scriptStart.setTextContent(scripts.getScriptAtBodyStart());
            element.insertBefore(scriptStart, element.getFirstChild());

            final Node scriptEnd = document.createElement("script");
            scriptStart.setTextContent(scripts.getScriptAtBodyEnd());
            element.appendChild(scriptEnd);

            return false;
        };
    }

}
