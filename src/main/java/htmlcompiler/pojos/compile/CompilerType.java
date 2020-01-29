package htmlcompiler.pojos.compile;

import htmlcompiler.compilers.html.*;
import htmlcompiler.pojos.library.LibraryArchive;
import htmlcompiler.tools.Logger;

public enum CompilerType {

    nop(NopCompiler::new),
    jsoup(JsoupCompiler::new),
    codelibs(CodelibsCompiler::new),
    htmlunit(HtmlUnitCompiler::new),
    cyberneko(CyberNekoCompiler::new);

    public interface HtmlCompilerContructor {
        HtmlCompiler newCompiler(Logger log, LibraryArchive archive, ChecksConfig checksConfiguration);
    }

    private final HtmlCompilerContructor constructor;
    CompilerType(final HtmlCompilerContructor constructor) {
        this.constructor = constructor;
    }

    public HtmlCompiler newHtmlCompiler(final Logger log, final LibraryArchive archive, final ChecksConfig checksConfiguration) {
        return constructor.newCompiler(log, archive, checksConfiguration);
    }

}
