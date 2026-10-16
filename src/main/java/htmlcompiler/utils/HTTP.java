package htmlcompiler.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public enum HTTP {;
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    public static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();

    public static boolean isUrl(final String url) {
        return url != null && (url.startsWith("https://") || url.startsWith("http://"));
    }

    public static HttpResponse<byte[]> fetch(final String url) throws IOException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .build();
        try {
            final HttpResponse<byte[]> response = HTTP.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() > 299)
                throw new IOException("HTTP " + response.statusCode() + " when downloading " + url);
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted during download of " + url);
        }
    }

    public static boolean hasCorsAllowed(final HttpResponse<?> response) {
        return response.headers().firstValue("Access-Control-Allow-Origin").filter("*"::equals).isPresent();
    }

}
