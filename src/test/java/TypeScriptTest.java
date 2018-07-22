import com.mangofactory.typescript.EcmaScriptVersion;
import com.mangofactory.typescript.TypescriptCompiler;

public class TypeScriptTest {

    private static final String code =
        "function greeter(person) {\n" +
        "    return \"Hello, \" + person;\n" +
        "}\n" +
        "var user = \"Jane User\";\n" +
        "document.body.innerHTML = greeter(user);\n";


    public static void main(final String... args) {
        final TypescriptCompiler typescript = new TypescriptCompiler();
        typescript.setEcmaScriptVersion(EcmaScriptVersion.ES5);
        typescript.compile(code);
    }

}
