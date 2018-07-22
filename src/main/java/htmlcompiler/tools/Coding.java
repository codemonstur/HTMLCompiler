package htmlcompiler.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public enum Coding {;

    private static MessageDigest digest;
    public static byte[] sha384(final byte[] data) throws NoSuchAlgorithmException {
        if (digest == null) digest = MessageDigest.getInstance("SHA-384");
        digest.reset();
        return digest.digest(data);
    }

    private static final Base64.Encoder encoder = Base64.getEncoder();
    public static String encodeBase64(final byte[] data) {
        return encoder.encodeToString(data);
    }

}
