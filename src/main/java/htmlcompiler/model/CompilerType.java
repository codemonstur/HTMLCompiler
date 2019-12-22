package htmlcompiler.model;

import htmlcompiler.compilers.html.CodelibsCompiler;
import htmlcompiler.compilers.html.HtmlCompiler;
import htmlcompiler.compilers.html.HtmlUnitCompiler;
import htmlcompiler.compilers.html.JsoupCompiler;
import htmlcompiler.tools.Logger;

import java.io.IOException;

public enum CompilerType {

    jsoup(JsoupCompiler::new),
    codelibs(CodelibsCompiler::new),
    htmlunit(HtmlUnitCompiler::new);

    public interface HtmlCompilerContructor {
        HtmlCompiler newCompiler(Logger log) throws IOException;
    }

    private final HtmlCompilerContructor constructor;
    CompilerType(final HtmlCompilerContructor constructor) {
        this.constructor = constructor;
    }

    public HtmlCompiler newHtmlCompiler(final Logger log) throws IOException {
        return constructor.newCompiler(log);
    }

}
