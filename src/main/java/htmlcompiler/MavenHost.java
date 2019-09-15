package htmlcompiler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import htmlcompiler.model.fakeapi.Endpoint;
import htmlcompiler.model.fakeapi.Request;
import htmlcompiler.tools.LogSuppressingMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static htmlcompiler.Tasks.*;
import static htmlcompiler.model.fakeapi.Endpoint.toKey;
import static htmlcompiler.model.fakeapi.Request.toHttpHandler;
import static htmlcompiler.tools.Logger.newLogger;
import static htmlcompiler.tools.Watcher.watchDirectory;

@Mojo( name = "host" )
public final class MavenHost extends LogSuppressingMojo  {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Parameter(defaultValue = "8080")
    public int port;

    @Parameter(defaultValue = "true")
    public boolean replaceExtension;

    @Parameter(defaultValue = "true")
    public boolean requestApiEnabled;

    @Parameter(defaultValue = "")
    public String requestApiSpecification;

    @Override
    public void execute() throws MojoFailureException {
        final Log log = getLog();

        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 100);
            server.createContext("/", newServerHandler(project, requestApiEnabled, requestApiSpecification));
            server.start();

            log.info("Listening on localhost:"+port);

            watchDirectory(toInputDirectory(project), () -> compileHTML(newLogger(log::info, log::warn), project, replaceExtension));
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    private static HttpHandler newServerHandler(final MavenProject project, final boolean specEnabled, final String specFile) throws MojoFailureException, IOException {
        final HttpHandler handler = pathHandler(toStaticDirectory(project), toOutputDirectory(project));
        return specEnabled ? fakeApiHandler(toApiMap(fileToSpec(specFile)), handler) : handler;
    }

    private static List<Request> fileToSpec(final String filename) throws IOException {
        final String data = Files.readString(Path.of(filename));
        return new Gson().fromJson(data, new TypeToken<List<Request>>(){}.getType());
    }
    private static Map<Endpoint, HttpHandler> toApiMap(final List<Request> requests) {
        final Map<Endpoint, HttpHandler> map = new HashMap<>();
        for (final Request spec : requests) {
            map.put(spec.endpoint, toHttpHandler(spec));
        }
        return map;
    }

    private static HttpHandler fakeApiHandler(final Map<Endpoint, HttpHandler> api, final HttpHandler next) {
        return exchange -> api.getOrDefault(toKey(exchange), next).handle(exchange);
    }

    private static HttpHandler pathHandler(final File... directories) {
        return exchange -> {
            if (!"/".equals(exchange.getRequestURI().getPath())) {
                for (final File dir : directories) {
                    final File file = toFile(dir, exchange.getRequestURI().getPath(), null);
                    if (file == null) continue;

                    exchange.getResponseHeaders().add("Content-Type", Files.probeContentType(file.toPath()));
                    exchange.sendResponseHeaders(200, file.length());
                    Files.copy(file.toPath(), exchange.getResponseBody());
                    exchange.close();
                    return;
                }
            }

            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        };
    }

    private static File toFile(final File dir, final String requestPath, final File _default) {
        final File requested = new File(dir, requestPath.substring(1));
        return (!isChildOf(requested, dir) || !requested.exists() || !requested.canRead()) ? _default : requested;
    }

    private static boolean isChildOf(final File child, final File parent) {
        return child.toPath().startsWith(parent.toPath());
    }
}
