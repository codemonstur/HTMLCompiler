package htmlcompiler.compilers.templates;

import de.neuland.jade4j.Jade4J;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Jade4j implements HtmlTemplateEngine {

    private final Map<String, Object> context;
    public Jade4j(final MavenProject project) {
        this.context = applyMavenProjectContext(applyEnvironmentContext(new HashMap<>()), project);
    }

    @Override
    public String compile(final File file) throws IOException {
        return Jade4J.render(file.getAbsolutePath(), context);
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
