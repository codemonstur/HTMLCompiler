package incubation;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class Hashing {
    private Hashing() {}

    public static void main(final String... args) throws NoSuchAlgorithmException, IOException {
//        final String url = "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css";
//        final String expected = "sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u";
//        final String result = "sha384-"+base64(sha384(download(url)));
//
//        System.out.println("Expected: " + expected);
//        System.out.println("Result  : " + result);
//        System.out.println("Equal   : " + expected.equals(result));
    }

    private static MessageDigest digest;
    private static byte[] sha384(final byte[] data) throws NoSuchAlgorithmException {
        if (digest == null) digest = MessageDigest.getInstance("SHA-384");
        digest.reset();
        return digest.digest(data);
    }

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static String base64(final byte[] data) {
        return encoder.encodeToString(data);
    }

/*
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
          crossorigin="anonymous">
*/

}
