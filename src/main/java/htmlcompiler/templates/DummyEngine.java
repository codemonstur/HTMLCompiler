package htmlcompiler.templates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class DummyEngine implements TemplateEngine {

    @Override
    public String processTemplate(File file) throws IOException {
        return Files.readString(file.toPath());
    }

}
