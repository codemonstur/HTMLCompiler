package bugs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileAndPath {

    public static void main(final String... args) {
        final File file = new File("/Users/jurgen/Projects/codesk/JadeTest/src/main/websrc/candidate.jade",
                "/Users/jurgen/Projects/codesk/JadeTest/src/main/webinc/js/public/candidate.js");
        final Path path = Paths.get("/Users/jurgen/Projects/codesk/JadeTest/src/main/websrc/candidate.jade")
                .resolve("/Users/jurgen/Projects/codesk/JadeTest/src/main/webinc/js/public/candidate.js");
        System.out.println(file.exists());
        System.out.println(Files.exists(path));
    }

}
