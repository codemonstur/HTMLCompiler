package incubation;

import java.io.IOException;

import static htmlcompiler.services.RepositoryVersions.checkVersionLibrary;
import static htmlcompiler.tools.Logger.newLogger;

public class CdnJsVersions {

    public static void main(final String... args) throws IOException, InterruptedException {
        final var logger = newLogger(System.out::println, System.err::println);
        checkVersionLibrary(logger, "file.html", "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/boe", false);
    }

}
