package htmlcompiler.compilers;

import java.io.IOException;

public interface Compressor {

    String compress(String code) throws IOException;

}