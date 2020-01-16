package htmlcompiler.compilers.templates;

import htmlcompiler.compilers.FileCompiler;

public interface HtmlTemplateEngine extends FileCompiler {

    default String outputExtension() {
        return ".html";
    }

}
