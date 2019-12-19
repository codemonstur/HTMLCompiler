package htmlcompiler.tools;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.FileNotFoundException;
import java.io.IOException;

public enum HTTP {;
    private static final OkHttpClient HTTP = new OkHttpClient();

    public static boolean isUrl(final String url) {
        return url != null && (url.startsWith("https://") || url.startsWith("http://"));
    }

    public static boolean urlHasCorsAllowed(final String url) {
        final Request request = new Request.Builder().url(url).build();
        try (final Response response = HTTP.newCall(request).execute()) {
            return response.isSuccessful() && "*".equals(response.header("Access-Control-Allow-Origin"));
        } catch (IOException e) {
            return false;
        }
    }

    public static byte[] urlToByteArray(final String location) throws IOException {
        final Request request = new Request.Builder().url(location).build();
        try (final Response response = HTTP.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new FileNotFoundException();
            return response.body().bytes();
        }
    }

}
