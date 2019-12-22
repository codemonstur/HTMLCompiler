package htmlcompiler.compilers;

import java.io.File;

import static htmlcompiler.tools.IO.relativize;

public interface RenameFile {
    File toOutputFile(File inputFile);

    static RenameFile defaultRenamer(final File inputDir, final File outputDir, final boolean replaceExtension) {
        return inputFile -> {
            final File outFile = new File(outputDir, extensionize(relativize(inputDir, inputFile), replaceExtension));
            outFile.getParentFile().mkdirs();
            return outFile;
        };
    }

    private static String extensionize(final String filename, final boolean replaceExtension) {
        return replaceExtension ? filename.substring(0, filename.lastIndexOf('.')) + ".html" : filename+".html";
    }
}

