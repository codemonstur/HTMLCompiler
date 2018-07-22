package htmlcompiler.model;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public final class JavascriptSet {
    @Parameter(defaultValue="true")
    public boolean compress = true;

    @Parameter
    public File rootDir;
    @Parameter
    public File jsFile;
    @Parameter
    public String outputDir;
}
