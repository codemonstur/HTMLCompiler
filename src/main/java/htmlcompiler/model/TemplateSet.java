package htmlcompiler.model;

import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

public final class TemplateSet {

    @Parameter(defaultValue="false")
    public boolean recursive = false;
    @Parameter(defaultValue="true")
    public boolean compress = true;
    @Parameter
    public String[] extensions;

    @Parameter
    public File rootDir;
    @Parameter
    public File templateDir;
    @Parameter
    public String outputDir;

    public static TemplateSet[] newDefaultTemplateSet(final MavenProject project) {
        final TemplateSet set = new TemplateSet();
        set.rootDir = new File(project.getBasedir()+"/src/main/websrc");
        set.templateDir = new File(project.getBasedir()+"/src/main/websrc");
        set.outputDir = project.getBuild().getOutputDirectory()+"/webbin";
        set.recursive = true;
        return new TemplateSet[] { set };
    }
}
