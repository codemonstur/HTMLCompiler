package example;

import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

// You can access compiled HTML files as resources in the jar. I use the JDK HttpServer here to serve it.
public enum Main {;

    public static void main(final String... args) throws IOException {
        final var page = loadResource("/individual/index.html");

        HttpServer
            .create(new InetSocketAddress("127.0.0.1", 8080), 100)
            .createContext("/", exchange -> {
                exchange.sendResponseHeaders(200, page.length);
                exchange.getResponseBody().write(page);
                exchange.close();
            })
            .getServer().start();
    }

    private static byte[] loadResource(final String resourcePath) throws IOException {
        final var in = Main.class.getResourceAsStream(resourcePath);
        if (in == null) throw new FileNotFoundException("Missing resource " + resourcePath);

        try (in) { return in.readAllBytes(); }
    }

}
