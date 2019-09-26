package htmlcompiler.compile.tags;

import htmlcompiler.model.ScriptBag;
import org.w3c.dom.Node;

public enum Body {;

    public static TagProcessor newBodyProcessor(final ScriptBag scripts) {
        return (file, document, element) -> {
            final String startCode = scripts.getScriptAtBodyStart().trim();
            if (!startCode.isEmpty()) {
                final Node scriptStart = document.createElement("script");
                scriptStart.setTextContent(startCode);
                element.insertBefore(scriptStart, element.getFirstChild());
            }

            final String endCode = scripts.getScriptAtBodyEnd().trim();
            if (!endCode.isEmpty()) {
                final Node scriptEnd = document.createElement("script");
                scriptEnd.setTextContent(endCode);
                element.appendChild(scriptEnd);
            }

            return false;
        };
    }

}
