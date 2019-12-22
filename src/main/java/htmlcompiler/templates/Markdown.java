package htmlcompiler.templates;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import htmlcompiler.error.InvalidTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class Markdown implements TemplateEngine {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public Markdown() {
        final MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    @Override
    public String processTemplate(File file) throws IOException, InvalidTemplate {
        try (final var reader = new FileReader(file)) {
            return renderer.render(parser.parseReader(reader));
        }
    }
}
