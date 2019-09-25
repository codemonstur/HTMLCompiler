import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import static util.DirectoryWatcher.newDirectoryWatcher;

public class TestDirectoryWatcher {

    public static void main(final String... args) throws IOException, InterruptedException {
        final var cwd = Paths.get(".");
        System.out.println("Listening for events in " + cwd.toFile().getAbsolutePath());

        newDirectoryWatcher(Set.of(cwd), (event, path) ->
                System.out.println("Received event " + event.name() + " for " + path))
            .start().join();
    }
}
