package incubation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsoupFragment {

    public static void main(final String... args) throws IOException {
        final File file = new File("htmlcompiler/src/test/resources/fragment/fragment1.html");
        final String content = Files.readString(file.toPath().toAbsolutePath());

        Element document = Jsoup.parseBodyFragment(content).body();
        System.out.println(document.html());
    }

}
