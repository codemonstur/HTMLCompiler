package htmlcompiler.compile.js;

import htmlcompiler.tools.IO;

import java.io.File;
import java.io.IOException;

public interface ScriptCompiler {

    String compileScript(String code, File parent) throws IOException;
    String compileScript(File script) throws IOException;

    public static ScriptCompiler newNopCompiler() {
        return new ScriptCompiler() {
            public String compileScript(String code, File parent) throws IOException {
                return code;
            }
            public String compileScript(File script) throws IOException {
                return IO.toString(script);
            }
        };
    }
}
