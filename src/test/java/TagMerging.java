import htmlcompiler.compile.HtmlCompiler;
import htmlcompiler.tools.Logger;

import java.io.File;
import java.nio.file.Files;

import static htmlcompiler.tools.Logger.newLogger;

public class TagMerging {

    public static void main(final String... args) throws Exception {
        final Logger log = newLogger(System.out::println, System.out::println);
        final HtmlCompiler html = new HtmlCompiler(log);

        final File file = new File("htmlcompiler/src/test/resources/merging/test2.html");
        final String content = Files.readString(file.toPath().toAbsolutePath());

        String s = html.compileHtmlCode(file, content);
        System.out.println(s);
    }

}
