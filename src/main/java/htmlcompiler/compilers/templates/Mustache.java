package htmlcompiler.compilers.templates;

import htmlcompiler.pojos.error.InvalidTemplate;
import org.apache.maven.project.MavenProject;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Mustache implements HtmlTemplateEngine {

    private final MustacheEngine engine;
    private final Map<String, Object> model;
    public Mustache(final MavenProject project) {
        this.engine = MustacheEngineBuilder.newBuilder().build();
        this.model = applyMavenProjectContext(applyEnvironmentContext(new HashMap<>()), project);
    }
    @Override
    public String compile(File file) throws IOException, InvalidTemplate {
        return engine.compileMustache(Files.readString(file.toPath())).render(model);
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
