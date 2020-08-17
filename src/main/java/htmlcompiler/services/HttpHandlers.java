package htmlcompiler.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.*;

public enum HttpHandlers {;

    public static HttpHandler pathHandler(final Path... directories) {
        return exchange -> {
            final var path = exchange.getRequestURI().getPath();
            if (!"/".equals(path)) {
                for (final Path dir : directories) {
                    final var file = toFile(dir, path, null);
                    if (file == null) continue;
                    sendFile(exchange, file);
                    return;
                }
            }
            send404(exchange);
        };
    }

    public static void sendFile(final HttpExchange exchange, final Path file) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", addCharset(probeContentType(file)));
        final long length = Files.size(file);
        if (length > 0) {
            exchange.sendResponseHeaders(200, length);
            Files.copy(file, exchange.getResponseBody());
        } else {
            exchange.sendResponseHeaders(200, -1);
        }
        exchange.close();
    }
    public static void send404(final HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, -1);
        exchange.close();
    }

    private static String addCharset(final String contentType) {
        if ("text/html".equals(contentType)) return "text/html; charset=UTF-8";
        if ("text/plain".equals(contentType)) return "text/plain; charset=UTF-8";
        return contentType;
    }

    public static Path toFile(final Path dir, final String requestPath, final Path defaultValue) {
        final Path requested = dir.resolve(requestPath.substring(1));
        return (!isChildOf(requested, dir) || !isRegularFile(requested) || !isReadable(requested)) ? defaultValue : requested;
    }

    private static boolean isChildOf(final Path child, final Path parent) {
        return child.startsWith(parent);
    }

}
