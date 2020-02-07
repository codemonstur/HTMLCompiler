package htmlcompiler.compilers.templates;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import htmlcompiler.pojos.error.InvalidTemplate;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.newBufferedReader;

public final class Markdown implements HtmlTemplateEngine {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public Markdown() {
        final MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    @Override
    public String compile(Path file) throws IOException, InvalidTemplate {
        try (final var reader = newBufferedReader(file)) {
            return renderer.render(parser.parseReader(reader));
        }
    }
}
