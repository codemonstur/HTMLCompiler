import htmlcompiler.compiler.CssCompiler;
import htmlcompiler.compiler.HtmlCompiler;
import htmlcompiler.compiler.JsCompiler;
import htmlcompiler.logging.Logger;
import htmlcompiler.logging.StdoutLogger;
import htmlcompiler.util.Loader;

import java.io.File;
import java.util.HashMap;

public class DebugTool {

    public static void main(final String... args) throws Exception {
        final Logger log = new StdoutLogger();
        final Loader loader = new Loader(".");
        final JsCompiler js = new JsCompiler(loader);
        final CssCompiler css = new CssCompiler(loader);
        final HtmlCompiler html = new HtmlCompiler(log, loader, css, js, new HashMap<>());

        final String page = html.compile(new File("/home/jurgen/private/experiments/CircuitbreakerDashboard/src/main/websrc/index.html"));
        System.out.println("done");
    }

}
