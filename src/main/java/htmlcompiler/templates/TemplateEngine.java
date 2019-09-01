package htmlcompiler.templates;

import htmlcompiler.error.TemplateParseException;

import java.io.File;
import java.io.IOException;

public interface TemplateEngine {

    String processTemplate(File file) throws IOException, TemplateParseException;

}
