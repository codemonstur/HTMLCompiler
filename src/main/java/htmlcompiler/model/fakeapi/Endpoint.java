package htmlcompiler.model.fakeapi;

import com.sun.net.httpserver.HttpExchange;

import java.util.Objects;

public final class Endpoint {

    private final String method;
    private final String path;

    private Endpoint(final String method, final String path) {
        this.method = method;
        this.path = path;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint apiKey = (Endpoint) o;
        return Objects.equals(method, apiKey.method) &&
                Objects.equals(path, apiKey.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }

    public static Endpoint toKey(final HttpExchange exchange) {
        return toKey(exchange.getRequestMethod(), exchange.getRequestURI().getPath());
    }
    public static Endpoint toKey(final String method, final String path) {
        return new Endpoint(method, path);
    }
}
