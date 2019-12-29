package htmlcompiler.model;

import htmlcompiler.compilers.html.*;
import htmlcompiler.library.LibraryArchive;
import htmlcompiler.tools.Logger;

import java.util.Map;

public enum CompilerType {

    jsoup(JsoupCompiler::new),
    codelibs(CodelibsCompiler::new),
    htmlunit(HtmlUnitCompiler::new),
    cyberneko(CyberNekoCompiler::new);

    public interface HtmlCompilerContructor {
        HtmlCompiler newCompiler(Logger log, LibraryArchive archive, Map<String, Boolean> checksConfiguration);
    }

    private final HtmlCompilerContructor constructor;
    CompilerType(final HtmlCompilerContructor constructor) {
        this.constructor = constructor;
    }

    public HtmlCompiler newHtmlCompiler(final Logger log, final LibraryArchive archive, final Map<String, Boolean> checksConfiguration) {
        return constructor.newCompiler(log, archive, checksConfiguration);
    }

}
