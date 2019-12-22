package htmlcompiler.templates;

import htmlcompiler.error.InvalidTemplate;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class JinJava implements TemplateEngine {

    private final com.hubspot.jinjava.Jinjava jinjava;
    private final Map<String, Object> context;

    public JinJava(final MavenProject project) {
        this.jinjava = new com.hubspot.jinjava.Jinjava();
        this.context = applyMavenProjectContext(applyEnvironmentContext(new HashMap<>()), project);
    }

    @Override
    public String processTemplate(File file) throws IOException, InvalidTemplate {
        return jinjava.render(Files.readString(file.toPath()), context);
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
