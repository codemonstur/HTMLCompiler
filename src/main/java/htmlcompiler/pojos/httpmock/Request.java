package htmlcompiler.pojos.httpmock;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import htmlcompiler.services.HttpHandlers;

import java.nio.file.Path;
import java.util.List;

import static htmlcompiler.services.HttpHandlers.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class Request {

    public final Endpoint endpoint;
    private final int statusCode;
    private final List<Header> headers;
    private final String body;

    public Request(final Endpoint endpoint, final int statusCode, final List<Header> headers, final String body) {
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static HttpHandler toHttpHandler(final Request request, final Path... directories) {
        if (request.statusCode == 604) { // 604 is 2 times a 302, we want to send the file at Location
            final var location = toLocationHeader(request);
            if (location == null) return HttpHandlers::send404;
            return exchange -> {
                for (final Path dir : directories) {
                    final var file = toFile(dir, location.value, null);
                    if (file == null) continue;
                    sendFile(exchange, file);
                    return;
                }
                send404(exchange);
            };
        } else return exchange -> {
            final Headers responseHeaders = exchange.getResponseHeaders();
            for (final Header header : request.headers) {
                if ("Content-Length".equalsIgnoreCase(header.name)) continue;
                responseHeaders.add(header.name, header.value);
            }
            final byte[] bodyBytes = request.body.getBytes(UTF_8);
            final int bodyLength = bodyBytes.length == 0 ? -1 : bodyBytes.length;

            exchange.sendResponseHeaders(request.statusCode, bodyLength);
            exchange.getResponseBody().write(bodyBytes);
            exchange.close();
        };
    }

    private static Header toLocationHeader(final Request request) {
        for (final var header : request.headers)
            if ("Location".equalsIgnoreCase(header.name))
                return header;
        return null;
    }
}
