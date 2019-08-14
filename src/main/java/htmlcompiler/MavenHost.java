package htmlcompiler;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
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

import static htmlcompiler.Tasks.*;
import static htmlcompiler.tools.Logger.newLogger;
import static htmlcompiler.tools.Watcher.watchDirectory;

@Mojo( name = "host" )
public final class MavenHost extends LogSuppressingMojo  {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Parameter(defaultValue = "8080")
    public int port;

    @Override
    public void execute() throws MojoFailureException {
        final Log log = getLog();

        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 100);
            server.createContext("/", pathHandler(toStaticDirectory(project), toOutputDirectory(project)));
            server.start();

            log.info("Listening on localhost:"+port);

            watchDirectory(toInputDirectory(project), () -> compileHTML(newLogger(log::info, log::warn), project));
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    private static HttpHandler pathHandler(final File... directories) {
        return exchange -> {
            for (final File dir : directories) {
                final File file = toFile(dir, exchange.getRequestURI().getPath(), null);
                if (file == null) continue;

                exchange.getResponseHeaders().add("Content-Type", Files.probeContentType(file.toPath()));
                exchange.sendResponseHeaders(200, file.length());
                Files.copy(file.toPath(), exchange.getResponseBody());
                exchange.close();
                return;
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
