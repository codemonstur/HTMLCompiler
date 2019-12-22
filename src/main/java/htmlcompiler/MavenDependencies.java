package htmlcompiler;

import htmlcompiler.tools.LogSuppressingMojo;
import htmlcompiler.tools.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.FileNotFoundException;

import static htmlcompiler.tools.App.buildMavenTask;
import static htmlcompiler.tools.IO.findBinaryInPath;

@Mojo( name = "dependencies" )
public final class MavenDependencies extends LogSuppressingMojo {

    @Override
    public void execute() throws MojoFailureException {
        buildMavenTask(this, log -> {
            testBinary(log, "TypeScript compiler", "tsc");
            testBinary(log, "JS++ compiler", "jspp");
        });
    }

    private void testBinary(final Logger log, final String name, final String binary) {
        try {
            findBinaryInPath(binary);
        } catch (FileNotFoundException e) {
            log.warn("Unable to find the " + name + ". Binary name is " + binary);
            e.printStackTrace();
        }
    }

}
