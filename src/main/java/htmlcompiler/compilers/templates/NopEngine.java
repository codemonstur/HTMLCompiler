package htmlcompiler.compilers.templates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NopEngine implements HtmlTemplateEngine {

    @Override
    public String compile(Path file) throws IOException {
        return Files.readString(file);
    }

}
