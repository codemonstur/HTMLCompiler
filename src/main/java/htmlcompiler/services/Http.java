package htmlcompiler.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
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
import static htmlcompiler.services.HttpHandlers.pathHandler;
import static xmlparser.utils.Functions.isNullOrEmpty;

public enum Http {;

    public static HttpServer newHttpServer(final int port, final boolean requestApiEnabled
            , final String requestApiSpecification, final Path... pathsToHost) throws IOException {
        HttpHandler handler = pathHandler(pathsToHost);
        if (requestApiEnabled) {
            handler = fakeApiHandler(toApiMap(fileToSpec(requestApiSpecification), pathsToHost), handler);
        }

        return HttpServer
            .create(new InetSocketAddress("127.0.0.1", port), 100)
            .createContext("/", handler)
            .getServer();
    }

    private static Map<String, List<Request>> fileToSpec(final String filename) throws IOException {
        final String data = Files.readString(Path.of(filename));
        return new Gson().fromJson(data, new TypeToken<Map<String, List<Request>>>(){}.getType());
    }

    private static Map<String, Map<Endpoint, HttpHandler>> toApiMap(final Map<String, List<Request>> requests
            , final Path... pathsToHost) {
        final var api = new HashMap<String, Map<Endpoint, HttpHandler>>();
        for (final var entry : requests.entrySet()) {
            final var handlers = new HashMap<Endpoint, HttpHandler>();
            for (final Request spec : entry.getValue()) {
                handlers.put(spec.endpoint, toHttpHandler(spec, pathsToHost));
            }
            api.put(entry.getKey(), handlers);
        }
        return api;
    }

    private static HttpHandler fakeApiHandler(final Map<String, Map<Endpoint, HttpHandler>> api, final HttpHandler next) {
        return exchange -> api.get(toVirtualHost(exchange)).getOrDefault(toKey(exchange), next).handle(exchange);
    }

    private static String toVirtualHost(final HttpExchange exchange) {
        final String host = exchange.getRequestHeaders().getFirst("Host");
        if (isNullOrEmpty(host)) return "";
        int colonIndex = host.indexOf(':');
        if (colonIndex == -1) return host;
        return host.substring(0, colonIndex);
    }

}
