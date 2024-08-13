package htmlcompiler.commands.maven;

import htmlcompiler.commands.Dependencies;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import static htmlcompiler.utils.App.buildMavenTask;

@Mojo( name = "dependencies" )
public final class MavenDependencies extends AbstractMojo {

    @Override
    public void execute() throws MojoFailureException {
        buildMavenTask(this, Dependencies::executeDependencies);
    }

}
