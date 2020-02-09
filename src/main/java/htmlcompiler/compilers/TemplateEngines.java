package htmlcompiler.compilers;

import com.github.jknack.handlebars.Template;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import de.neuland.jade4j.Jade4J;
import htmlcompiler.pojos.error.InvalidTemplate;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.nio.file.Files.newBufferedReader;

public enum TemplateEngines {;

    public interface HtmlTemplateEngine extends FileCompiler {
        default String outputExtension() {
            return ".html";
        }
    }

    public static HtmlTemplateEngine newHandlebarsEngine(final Map<String, String> context) {
        final var handlebars = new com.github.jknack.handlebars.Handlebars();
        return file -> {
            final Template template = handlebars.compile(file.toAbsolutePath().toString());
            return template.apply(context);
        };
    }

    public static HtmlTemplateEngine newJade4jEngine(final Map<String, String> context) {
        return file -> Jade4J.render(file.toAbsolutePath().toString(), Collections.unmodifiableMap(context));
    }

    public static HtmlTemplateEngine newJinJavaEngine(final Map<String, String> context) {
        final var jinjava = new com.hubspot.jinjava.Jinjava();
        return file -> jinjava.render(Files.readString(file), context);
    }

    public static HtmlTemplateEngine newJTwigEngine(final Map<String, String> context) {
        return file -> {
            final JtwigModel model = JtwigModel.newModel(Collections.unmodifiableMap(context));
            return JtwigTemplate.fileTemplate(file.toFile()).render(model);
        };
    }

    public static HtmlTemplateEngine newMarkdownEngine() {
        final MutableDataSet options = new MutableDataSet();
        final Parser parser = Parser.builder(options).build();;
        final HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        return file -> {
            try (final var reader = newBufferedReader(file)) {
                return renderer.render(parser.parseReader(reader));
            }
        };
    }

    public static HtmlTemplateEngine newMustacheEngine(final Map<String, String> context) {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
        return file -> engine.compileMustache(Files.readString(file)).render(context);
    }

    public static HtmlTemplateEngine newPebbleEngine(final Map<String, String> context) {
        final PebbleEngine pebble = new PebbleEngine.Builder().build();
        return file -> {
            try {
                final PebbleTemplate template = pebble.getTemplate(file.toAbsolutePath().toString());
                try (final StringWriter writer = new StringWriter()) {
                    template.evaluate(writer, Collections.unmodifiableMap(context));
                    return writer.toString();
                }
            } catch (PebbleException e) {
                throw new InvalidTemplate(e);
            }
        };
    }

    public static HtmlTemplateEngine newThymeleafEngine(final Map<String, String> context) {
        final var engine = new TemplateEngine();
        return file -> engine.process(Files.readString(file), toThymeleafContext(context));
    }

    public static IContext toThymeleafContext(final Map<String, String> map) {
        return new IContext() {
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

}
