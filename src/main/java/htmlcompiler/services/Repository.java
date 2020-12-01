package htmlcompiler.services;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.*;

public enum Repository {;

    public static Path getRepositoryDirectory() throws IOException {
        final Path repo = Paths.get(System.getProperty("user.home")).resolve(".htmlcompiler");
        if (!exists(repo)) createDirectories(repo);
        if (!isDirectory(repo)) throw new IOException("'~/.htmlcompiler' is not a directory");
        return repo;
    }

}
