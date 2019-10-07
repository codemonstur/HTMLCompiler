package example;

import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

public enum Main {;

    public static void main(final String... args) throws IOException {
        final byte[] page = resourceToString("/webbin/index.html").getBytes(UTF_8);

        HttpServer
            .create(new InetSocketAddress("127.0.0.1", 8080), 100)
            .createContext("/", exchange -> {
                exchange.sendResponseHeaders(200, page.length);
                exchange.getResponseBody().write(page);
                exchange.close();
            })
            .getServer().start();
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
