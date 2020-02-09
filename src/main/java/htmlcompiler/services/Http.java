package htmlcompiler.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import htmlcompiler.pojos.httpmock.Endpoint;
import htmlcompiler.pojos.httpmock.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static htmlcompiler.pojos.httpmock.Endpoint.toKey;
import static htmlcompiler.pojos.httpmock.Request.toHttpHandler;
import static java.nio.file.Files.*;

public enum Http {;

    public static HttpServer newHttpServer(final int port, final boolean requestApiEnabled
            , final String requestApiSpecification, final Path... pathsToHost) throws IOException {
        HttpHandler handler = pathHandler(pathsToHost);
        if (requestApiEnabled) {
            handler = fakeApiHandler(toApiMap(fileToSpec(requestApiSpecification)), handler);
        }

        return HttpServer
            .create(new InetSocketAddress("127.0.0.1", port), 100)
            .createContext("/", handler)
            .getServer();
    }

    private static List<Request> fileToSpec(final String filename) throws IOException {
        final String data = Files.readString(Path.of(filename));
        return new Gson().fromJson(data, new TypeToken<List<Request>>(){}.getType());
    }

    private static Map<Endpoint, HttpHandler> toApiMap(final List<Request> requests) {
        final Map<Endpoint, HttpHandler> map = new HashMap<>();
        for (final Request spec : requests) {
            map.put(spec.endpoint, toHttpHandler(spec));
        }
        return map;
    }

    private static HttpHandler fakeApiHandler(final Map<Endpoint, HttpHandler> api, final HttpHandler next) {
        return exchange -> api.getOrDefault(toKey(exchange), next).handle(exchange);
    }

    private static HttpHandler pathHandler(final Path... directories) {
        return exchange -> {
            if (!"/".equals(exchange.getRequestURI().getPath())) {
                for (final Path dir : directories) {
                    final Path file = toFile(dir, exchange.getRequestURI().getPath(), null);
                    if (file == null) continue;

                    exchange.getResponseHeaders().add("Content-Type", addCharset(probeContentType(file)));
                    final long length = Files.size(file);
                    if (length > 0) {
                        exchange.sendResponseHeaders(200, length);
                        Files.copy(file, exchange.getResponseBody());
                    } else {
                        exchange.sendResponseHeaders(200, -1);
                    }
                    exchange.close();
                    return;
                }
            }

            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        };
    }

    private static String addCharset(final String contentType) {
        if ("text/html".equals(contentType)) return "text/html; charset=UTF-8";
        if ("text/plain".equals(contentType)) return "text/plain; charset=UTF-8";
        return contentType;
    }

    private static Path toFile(final Path dir, final String requestPath, final Path defaultValue) {
        final Path requested = dir.resolve(requestPath.substring(1));
        return (!isChildOf(requested, dir) || !isRegularFile(requested) || !isReadable(requested)) ? defaultValue : requested;
    }

    private static boolean isChildOf(final Path child, final Path parent) {
        return child.startsWith(parent);
    }
}
