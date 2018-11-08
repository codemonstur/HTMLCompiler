package htmlcompiler;

import htmlcompiler.tools.LogSuppressingMojo;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

import static htmlcompiler.Tasks.*;
import static htmlcompiler.tools.Logger.newLogger;
import static htmlcompiler.tools.Watcher.watchDirectory;
import static io.undertow.UndertowOptions.ALWAYS_SET_KEEP_ALIVE;
import static io.undertow.UndertowOptions.ENABLE_HTTP2;

@Mojo( name = "host" )
public final class MavenHost extends LogSuppressingMojo  {

    @Parameter( defaultValue = "${project}", readonly = true )
    public MavenProject project;

    @Override
    public void execute() throws MojoFailureException {

        Undertow.builder()
            .setServerOption(ALWAYS_SET_KEEP_ALIVE, true)
            .setServerOption(ENABLE_HTTP2, true)
            .addHttpListener( 8080, "127.0.0.1")
            .setHandler(resourceHandler(toOutputDirectory(project)))
            .build().start();

        final Log log = getLog();
        try {
            watchDirectory(toInputDirectory(project), () -> compileHTML(newLogger(log::info, log::warn), project));
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    private static HttpHandler resourceHandler(final File directory) {
        return new ResourceHandler(new PathResourceManager(directory.toPath()))
                .setDirectoryListingEnabled(false).setWelcomeFiles();
    }

}
