package example;

import io.undertow.Undertow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public enum Main {;

    public static void main(final String... args) throws IOException {
        final String page = resourceToString("/webbin/index.html");

        Undertow.builder()
            .addHttpListener(8080, "127.0.0.1")
            .setHandler(e -> e.getResponseSender().send(page))
            .build().start();
    }

    private static String resourceToString(final String resource) throws IOException {
        final StringBuilder sb = new StringBuilder();

        try (final BufferedReader br = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(resource), UTF_8))) {
            for (int c = br.read(); c != -1; c = br.read()) {
                sb.append((char)c);
            }
        }

        return sb.toString();
    }
}
