package htmlcompiler.compilers.templates;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import htmlcompiler.pojos.error.InvalidTemplate;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Pebble implements HtmlTemplateEngine {

    private final PebbleEngine pebble;
    private final Map<String, Object> context;

    public Pebble(final MavenProject project) {
        this.pebble = newDefaultPebble();
        this.context = applyMavenProjectContext(applyEnvironmentContext(new HashMap<>()), project);
    }

    private static PebbleEngine newDefaultPebble() {
        return new PebbleEngine.Builder().build();
    }

    public String compile(final File file) throws IOException, InvalidTemplate {
        try {
            final PebbleTemplate template = pebble.getTemplate(file.getAbsolutePath());
            try (final StringWriter writer = new StringWriter()) {
                template.evaluate(writer, context);
                return writer.toString();
            }
        } catch (PebbleException e) {
            throw new InvalidTemplate(e);
        }
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
