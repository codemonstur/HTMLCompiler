package tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileContentType {

    public static void main(final String... args) throws IOException {
        String s = Files.probeContentType(Paths.get("src/test/resources/html/compress-html.html"));
        System.out.println(s);
    }
}
