package incubation;

import java.io.FileNotFoundException;

import static htmlcompiler.utils.IO.findBinaryInPath;

public class LocationBinary {

    public static void main(final String... args) throws FileNotFoundException {
        System.out.println(findBinaryInPath("tsc"));
    }

}
