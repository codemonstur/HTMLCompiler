package htmlcompiler.compilers.scripts;

import java.io.IOException;

public interface Compressor {

    String compress(String code) throws IOException;

}