package htmlcompiler.compile.html;

import htmlcompiler.model.ScriptBag;
import org.w3c.dom.Node;

public enum Head {;

    public static TagProcessor newHeadProcessor(final HtmlCompiler html, final ScriptBag scripts) {
        return (inputDir, file, document, element) -> {
            final Node scriptStart = document.createElement("script");
            scriptStart.setTextContent(scripts.getScriptAtHeadStart());
            element.insertBefore(scriptStart, element.getFirstChild());

            final Node scriptEnd = document.createElement("script");
            scriptStart.setTextContent(scripts.getScriptAtHeadEnd());
            element.appendChild(scriptEnd);

            return false;
        };
    }

}
