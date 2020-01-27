package htmlcompiler.compilers.templates;

import htmlcompiler.pojos.error.InvalidTemplate;
import org.apache.maven.project.MavenProject;
import org.thymeleaf.context.IContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Thymeleaf implements HtmlTemplateEngine {

    private org.thymeleaf.TemplateEngine engine;
    private IContext context;
    public Thymeleaf(final MavenProject project) {
        this.engine = new org.thymeleaf.TemplateEngine();
        final Map<String, Object> map = applyMavenProjectContext(applyEnvironmentContext(new HashMap<>()), project);
        this.context = new IContext() {
            public Locale getLocale() {
                return Locale.getDefault();
            }
            public boolean containsVariable(String s) {
                return map.containsKey(s);
            }
            public Set<String> getVariableNames() {
                return map.keySet();
            }
            public Object getVariable(String s) {
                return map.get(s);
            }
        };
    }

    @Override
    public String compile(Path file) throws IOException, InvalidTemplate {
        engine.process(Files.readString(file), context);
        return null;
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
