package htmlcompiler.templates;

import htmlcompiler.error.TemplateParseException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;

public interface TemplateEngine {

    String processTemplate(File file) throws IOException, TemplateParseException;

    public static Map<String, TemplateEngine> newExtensionToEngineMap(final MavenProject project) {
        return Map.ofEntries
            ( entry(".pebble", new Pebble(project))
            , entry(".jade", new Jade4j(project))
            , entry(".pug", new Jade4j(project))
            , entry(".md", new Markdown())
            , entry(".hb", new Handlebars(project))
            , entry(".jinjava", new JinJava(project))
            , entry(".twig", new JTwig(project))
            , entry(".mustache", new Mustache(project))
            , entry(".thymeleaf", new Thymeleaf(project))
            , entry(".hct", new DummyEngine())
            );
    }
}
