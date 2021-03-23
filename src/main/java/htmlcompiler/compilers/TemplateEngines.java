package htmlcompiler.compilers;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import de.neuland.jade4j.Jade4J;
import de.neuland.pug4j.Pug4J;
import htmlcompiler.pojos.error.InvalidTemplate;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

public enum TemplateEngines {;

    public interface HtmlTemplateEngine extends FileCompiler {
        default String outputExtension() {
            return ".html";
        }
    }

    public static HtmlTemplateEngine newJade4jEngine(final Map<String, String> context) {
        return file -> Jade4J.render(file.toAbsolutePath().toString(), Collections.unmodifiableMap(context));
    }

    public static HtmlTemplateEngine newPug4jEngine(final Map<String, String> context) {
        return file -> Pug4J.render(file.toAbsolutePath().toString(), Collections.unmodifiableMap(context));
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

}
