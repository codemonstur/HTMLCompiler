package tools;

import htmlcompiler.utils.Filenames;

import java.nio.file.Paths;

public enum CurrentWorkingDirectory {;

    public static void main(final String... args) {
        String path = Filenames.toRelativePath(Paths.get("C:\\windows"));
        System.out.println(path);
    }

}
