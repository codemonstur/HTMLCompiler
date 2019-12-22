package htmlcompiler.compilers.js;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public enum JsCompiler {;

    public static String compressJavascriptCode(final String code) throws IOException {
        final JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(code), null);
        final StringWriter writer = new StringWriter();
        compressor.compress(writer, -1, true, false, false, false);
        return writer.toString();
    }

}
