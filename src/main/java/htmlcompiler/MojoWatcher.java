package htmlcompiler;

import htmlcompiler.logging.Logger;
import htmlcompiler.logging.MavenLogger;
import htmlcompiler.model.TemplateSet;
import htmlcompiler.util.Watcher.FileEventListener;
import org.apache.log4j.Level;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static htmlcompiler.compiler.CompileAll.compileTemplates;
import static htmlcompiler.util.Watcher.infiniteThread;
import static htmlcompiler.util.Watcher.watchDirectory;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Mojo( name = "watch" )
public final class MojoWatcher extends AbstractMojo {
    static {
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.OFF);
    }

    @Parameter( defaultValue = "${project}", readonly = true )
    public MavenProject project;
    @Parameter( property = "outputdir" )
    public String outputDir;
    @Parameter
    public TemplateSet[] templateSets;

    @Override
    public void execute() {
        if (templateSets == null) {
            templateSets = TemplateSet.newDefaultTemplateSet(project);
        }

        try {
            watchProject();
        } catch (Exception e) {}
    }

    private void watchProject() throws IOException {
        final Logger log = new MavenLogger(getLog());
        final Semaphore doCompile = new Semaphore(0);

        final List<FileEventListener> listeners = new ArrayList<>();
        listeners.add((type, entry) -> doCompile.release());

        infiniteThread(() -> {
            doCompile.acquire();
            try {
                doCompile.drainPermits();
                for (final TemplateSet set : templateSets) {
                    compileTemplates(log, set, project, outputDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        watchDirectory(listeners, stream(templateSets).map(templateSet -> templateSet.rootDir).collect(toList()));
    }
}
