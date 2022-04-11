package htmlcompiler.compilers;

import htmlcompiler.tools.Logger;

import java.io.IOException;

public interface Compressor {

    String compress(Logger log, String code) throws IOException;

}