package htmlcompiler;

import bobthebuildtool.pojos.buildfile.Project;
import bobthebuildtool.pojos.error.VersionTooOld;
import bobthebuildtool.services.Log;
import bobthebuildtool.services.Task;
import htmlcompiler.commands.Compile;
import htmlcompiler.commands.Dependencies;
import htmlcompiler.commands.Host;
import jcli.errors.InvalidCommandLine;

import java.io.IOException;
import java.util.Map;

import static bobthebuildtool.services.Update.requireBobVersion;
import static htmlcompiler.CliHtmlCompile.newCompileCommandConfig;
import static htmlcompiler.CliHtmlHost.newHostCommandConfig;
import static htmlcompiler.commands.Compile.executeCompile;
import static htmlcompiler.commands.Dependencies.executeDependencies;
import static htmlcompiler.commands.Host.executeHost;
import static htmlcompiler.utils.Logger.newLogger;

public enum BobPlugin {;

    private static final String
        DESCRIPTION_COMPILE = "Compiles templates and plain HTML to compiled HTML",
        DESCRIPTION_HOST = "Monitors frontend code for changes and compiles and hosts on localhost:8080",
        DESCRIPTION_CHECK = "Checks if various needed binaries are available in the path";

    public static void installPlugin(final Project project) throws VersionTooOld {
        requireBobVersion("11");
        project.addCommand("compile-html", DESCRIPTION_COMPILE, BobPlugin::compileHtml);
        project.addCommand("host-frontend", DESCRIPTION_HOST, BobPlugin::hostFrontend);
        project.addCommand("check-frontend-dependencies", DESCRIPTION_CHECK, BobPlugin::checkDependencies);
    }

    private static int compileHtml(final Project project, final Map<String, String> environment, final String[] args)
            throws InvalidCommandLine, IOException {
        executeCompile(newLogger(Log::logInfo, Log::logWarning, Log::logError), newCompileCommandConfig(project, environment, args));
        return 0;
    }

    private static int hostFrontend(final Project project, final Map<String, String> environment, final String[] args)
            throws IOException, InterruptedException, InvalidCommandLine {
        executeHost(newLogger(Log::logInfo, Log::logWarning, Log::logError), newHostCommandConfig(project, environment, args));
        return 0;
    }

    private static int checkDependencies(final Project project, final Map<String, String> environment, final String[] args) {
        executeDependencies(newLogger(Log::logInfo, Log::logWarning, Log::logError));
        return 0;
    }

}
