package htmlcompiler.model;

import htmlcompiler.compilers.html.*;
import htmlcompiler.tools.Logger;

public enum CompilerType {

    jsoup(JsoupCompiler::new),
    codelibs(CodelibsCompiler::new),
    htmlunit(HtmlUnitCompiler::new),
    cyberneko(CyberNekoCompiler::new);

    public interface HtmlCompilerContructor {
        HtmlCompiler newCompiler(Logger log);
    }

    private final HtmlCompilerContructor constructor;
    CompilerType(final HtmlCompilerContructor constructor) {
        this.constructor = constructor;
    }

    public HtmlCompiler newHtmlCompiler(final Logger log) {
        return constructor.newCompiler(log);
    }

}
