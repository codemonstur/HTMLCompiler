package htmlcompiler.pojos.httpmock;

import com.sun.net.httpserver.HttpExchange;

public record Endpoint(String method, String path) {

    public static Endpoint toKey(final HttpExchange exchange) {
        return toKey(exchange.getRequestMethod(), exchange.getRequestURI().getPath());
    }
    public static Endpoint toKey(final String method, final String path) {
        return new Endpoint(method, path);
    }
}
