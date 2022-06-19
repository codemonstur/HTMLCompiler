package htmlcompiler.tools;

import java.nio.charset.Charset;
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

    public static byte[] sha256(final String data, final Charset charset) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data.getBytes(charset));
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Missing SHA-256 digest", e);
        }
    }

    private static final char[] hex_array = "0123456789abcdef".toCharArray();
    public static String encodeHex(final byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hex_array[v >>> 4];
            hexChars[j * 2 + 1] = hex_array[v & 0x0F];
        }
        return new String(hexChars);
    }

}
