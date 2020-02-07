package htmlcompiler.compilers.templates;

import com.github.jknack.handlebars.Template;
import htmlcompiler.pojos.error.InvalidTemplate;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Handlebars implements HtmlTemplateEngine {

    private final com.github.jknack.handlebars.Handlebars handlebars;
    private final Map<String, Object> context;

    public Handlebars(final MavenProject project) {
        this.handlebars = new com.github.jknack.handlebars.Handlebars();
        this.context = applyMavenProjectContext(applyEnvironmentContext(new HashMap<>()), project);
    }

    @Override
    public String compile(Path file) throws IOException, InvalidTemplate {
        final Template template = handlebars.compile(file.toAbsolutePath().toString());
        return template.apply(context);
    }

    private static Map<String, Object> applyMavenProjectContext(final Map<String, Object> context, final MavenProject project) {
        for (final Entry<Object, Object> entry : project.getProperties().entrySet()) {
            context.put(entry.getKey().toString(), entry.getValue());
        }
        return context;
    }
    private static Map<String, Object> applyEnvironmentContext(final Map<String, Object> context) {
        for (final Entry<String, String> entry : System.getenv().entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
        return context;
    }
}
