package incubation;

import htmlcompiler.utils.Logger;

import java.io.IOException;

import static htmlcompiler.services.RepositoryVersions.checkVersionLibrary;

public class CdnJsVersions {

    public static void main(final String... args) throws IOException, InterruptedException {
        final var logger = Logger.newLogger(System.out::println, System.err::println, System.err::println);
        checkVersionLibrary(logger, "file.html", "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/boe", false);
    }

}
