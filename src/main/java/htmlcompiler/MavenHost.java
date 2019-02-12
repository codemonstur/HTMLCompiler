package htmlcompiler;

import htmlcompiler.tools.LogSuppressingMojo;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

import static htmlcompiler.Tasks.*;
import static htmlcompiler.tools.Logger.newLogger;
import static htmlcompiler.tools.Watcher.watchDirectory;
import static io.undertow.UndertowOptions.ALWAYS_SET_KEEP_ALIVE;
import static io.undertow.UndertowOptions.ENABLE_HTTP2;

@Mojo( name = "host" )
public final class MavenHost extends LogSuppressingMojo  {

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Parameter(defaultValue = "8080")
    public int port;

    @Override
    public void execute() throws MojoFailureException {
        final Log log = getLog();

        Undertow.builder()
            .setServerOption(ALWAYS_SET_KEEP_ALIVE, true)
            .setServerOption(ENABLE_HTTP2, true)
            .addHttpListener(port, "127.0.0.1")
            .setHandler(pathHandler(toStaticDirectory(project), toOutputDirectory(project)))
            .build().start();

        log.info("Listening on localhost:"+port);

        try {
            watchDirectory(toInputDirectory(project), () -> compileHTML(newLogger(log::info, log::warn), project));
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    private static HttpHandler pathHandler(final File... directories) {
        return new BlockingHandler(exchange -> {
            for (final File dir : directories) {
                final File file = toFile(dir, exchange.getRequestPath(), null);
                if (file == null) continue;

                exchange.setStatusCode(StatusCodes.OK);
                exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, Files.probeContentType(file.toPath()));
                exchange.getResponseSender().transferFrom(toFileChannel(file), null);
                return;
            }

            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        });
    }

    private static File toFile(final File dir, final String requestPath, final File _default) {
        final File requested = new File(dir, requestPath.substring(1));
        return (!isChildOf(requested, dir) || !requested.exists() || !requested.canRead()) ? _default : requested;
    }

    private static boolean isChildOf(final File child, final File parent) {
        return child.toPath().startsWith(parent.toPath());
    }

    private static FileChannel toFileChannel(final File file) throws FileNotFoundException {
        return new RandomAccessFile(file, "r").getChannel();
    }
}
