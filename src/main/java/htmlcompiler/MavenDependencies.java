package htmlcompiler;

import htmlcompiler.tools.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import static htmlcompiler.tools.App.buildMavenTask;
import static htmlcompiler.tools.IO.findBinaryInPath;

@Mojo( name = "dependencies" )
public final class MavenDependencies extends AbstractMojo {

    @Override
    public void execute() throws MojoFailureException {
        buildMavenTask(this, log -> {
            testBinary(log, "TypeScript compiler", "tsc");
            testBinary(log, "Dart compiler", "dart2js");
            testBinary(log, "JS++ compiler", "js++");
            testBinary(log, "Stylus compiler", "stylus");
        });
    }

    private Path testBinary(final Logger log, final String name, final String binary) {
        try {
            return findBinaryInPath(binary);
        } catch (FileNotFoundException e) {
            log.warn("Unable to find the " + name + ". Binary name is " + binary);
            return null;
        }
    }

}
