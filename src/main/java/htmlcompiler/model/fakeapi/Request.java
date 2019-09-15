package htmlcompiler.model.fakeapi;

import com.sun.net.httpserver.HttpHandler;

import java.util.List;

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

    private static class Header {
        private final String name;
        private final String value;

        private Header(final String name, final String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static HttpHandler toHttpHandler(final Request request) {
        return exchange -> {
            for (final Header header : request.headers) {
                exchange.getResponseHeaders().add(header.name, header.value);
            }
            exchange.sendResponseHeaders(request.statusCode, request.body.length());
            exchange.getResponseBody().write(request.body.getBytes(UTF_8));
            exchange.close();
        };
    }
}
