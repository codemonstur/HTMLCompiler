package htmlcompiler.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

public enum HTTP {;
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    public static boolean isUrl(final String url) {
        return url != null && (url.startsWith("https://") || url.startsWith("http://"));
    }

    public static boolean urlHasCorsAllowed(final String url) {
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        try {
            final var response = HTTP.send(request, BodyHandlers.discarding());
            if (response.statusCode() < 200 || response.statusCode() > 299) return false;
            final var acao = response.headers().firstValue("Access-Control-Allow-Origin");
            return  acao.isPresent() && "*".equals(acao.get());
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public static byte[] urlToByteArray(final String url) throws IOException {
        final var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        try {
            final var response = HTTP.send(request, BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() > 299)
                throw new FileNotFoundException(url);
            return response.body();
        } catch (InterruptedException e) {
            throw new IOException("Interrupted during download of " + url);
        }
    }

}
