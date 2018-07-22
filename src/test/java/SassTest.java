import htmlcompiler.compiler.CssCompiler;
import htmlcompiler.error.UnrecognizedFileType;
import htmlcompiler.util.IO;
import htmlcompiler.util.Loader;
import org.lesscss.LessException;

import java.io.File;
import java.io.IOException;

public final class SassTest {
    private SassTest() {}

    public static void main(final String... args) throws LessException, IOException, UnrecognizedFileType {
        final CssCompiler css = new CssCompiler(new Loader("."));

        System.out.println(css.compile("src/test/resources/sass/test1.scss"));
        System.out.println(css.compile("src/test/resources/sass/test2.sass"));
        System.out.println(CssCompiler.compileScss(IO.toString(new File("src/test/resources/sass/test2.sass"))));
    }
}
