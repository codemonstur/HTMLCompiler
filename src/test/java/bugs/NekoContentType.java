package bugs;

public class NekoContentType {

    // All versions of NekoHtml I tested add a META tag to the output:
    //
    // <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
    //
    // Its a major eye sore, and a completely pointless addition. Properly
    // configured HTTP servers will send this as a header.
    // I'd love to remove it but I have no idea how. Jsoup doesn't add this
    // but of course Jsoup comes with its own problems.
    //

    public static void main(final String... args) {

    }

}
