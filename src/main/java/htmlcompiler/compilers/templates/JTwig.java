package htmlcompiler.compilers.templates;

import htmlcompiler.pojos.error.InvalidTemplate;
import org.apache.maven.project.MavenProject;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class JTwig implements HtmlTemplateEngine {

    private final JtwigModel model;
    public JTwig(final MavenProject project) {
        this.model = JtwigModel.newModel(applyMavenProjectContext(applyEnvironmentContext(new HashMap<>()), project));
    }

    @Override
    public String compile(Path file) throws IOException, InvalidTemplate {
        return JtwigTemplate.fileTemplate(file.toFile()).render(model);
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
