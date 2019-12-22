package htmlcompiler.compilers;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;

public enum MavenProjectReader {;

    public static File toInputDirectory(final MavenProject project) throws MojoFailureException {
        final File inputDir = new File(project.getBasedir(), "src/main/websrc");
        if (!inputDir.exists())
            throw new MojoFailureException("Input directory must exist: " + inputDir);
        if (!inputDir.isDirectory())
            throw new MojoFailureException("Input directory must be a directory");
        return inputDir;
    }

    public static File toOutputDirectory(final MavenProject project) throws MojoFailureException {
        final File outputDir = new File(project.getBuild().getOutputDirectory(), "webbin");
        outputDir.mkdirs();
        if (!outputDir.exists())
            throw new MojoFailureException("Output directory must exist: " + outputDir);
        if (!outputDir.isDirectory())
            throw new MojoFailureException("Output directory must be a directory");
        return outputDir;
    }

    public static File toStaticDirectory(final MavenProject project) throws MojoFailureException {
        final File outputDir = new File(project.getBuild().getOutputDirectory(), "wwwroot");
        outputDir.mkdirs();
        if (!outputDir.exists())
            throw new MojoFailureException("Output directory must exist: " + outputDir);
        if (!outputDir.isDirectory())
            throw new MojoFailureException("Output directory must be a directory");
        return outputDir;
    }

}
