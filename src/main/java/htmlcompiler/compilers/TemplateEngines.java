package htmlcompiler.compilers;

import de.neuland.pug4j.PugConfiguration;
import de.neuland.pug4j.template.FileTemplateLoader;
import de.neuland.pug4j.template.PugTemplate;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import de.neuland.jade4j.Jade4J;
import de.neuland.pug4j.Pug4J;
import htmlcompiler.pojos.error.InvalidTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;

public enum TemplateEngines {;

    public interface HtmlTemplateEngine extends FileCompiler {
        default String outputExtension() {
            return ".html";
        }
    }

    public static HtmlTemplateEngine newJade4jEngine(final Map<String, String> context) {
        return file -> Jade4J.render(file.toAbsolutePath().toString(), Collections.unmodifiableMap(context));
    }

    public static HtmlTemplateEngine newPug4jEngine(final String rootPath, final Map<String, String> context) {
        
        return file -> Pug4J.render(file.toAbsolutePath().toString(), Collections.unmodifiableMap(context));
    }

    private static PugTemplate loadTemplate(final String rootPath, final String filePath) throws IOException {
        final var templateFile = Paths.get(rootPath).resolve(filePath);
        if (!exists(templateFile)) throw new FileNotFoundException("No template found at " + templateFile);
        if (!isRegularFile(templateFile)) throw new IOException("Path to template is not a file " + templateFile);

        final var fileLoader = new FileTemplateLoader(rootPath);
        fileLoader.setBase(filePath.substring(0, filePath.lastIndexOf('/')));

        final var config = new PugConfiguration();
        config.setTemplateLoader(fileLoader);

        return config.getTemplate(File.separator + templateFile.getFileName());
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
