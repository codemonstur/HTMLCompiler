package htmlcompiler.compilers.templates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class NopEngine implements HtmlTemplateEngine {

    @Override
    public String compile(File file) throws IOException {
        return Files.readString(file.toPath());
    }

}
