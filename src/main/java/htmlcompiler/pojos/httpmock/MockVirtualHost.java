package htmlcompiler.pojos.httpmock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public final class MockVirtualHost {

    public static List<Header> redirectHeader(final String path) {
        return asList(new Header("Location", path));
    }
    public static List<Header> newRecordsHeader(final int count) {
        return asList(new Header("Number-Of-Records", String.valueOf(count)));
    }
    public static final List<Header>
        NO_HEADERS = emptyList(),
        JSON_CONTENT = asList(new Header("Content-Type", "application/json")),
        TEXT_CONTENT = asList(new Header("Content-Type", "text/plain; charset=UTF-8")),
        IMAGE_JPEG = asList(new Header("Content-Type", "image/jpeg")),
        TEXT_CSS = asList(new Header("Content-Type", "text/css; charset=UTF-8"));

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final List<Request> requests = new ArrayList<>();

    public MockVirtualHost apply(Consumer<MockVirtualHost> consumer) {
        consumer.accept(this);
        return this;
    }

    public MockVirtualHost head(final String path, final int statusCode, final List<Header> headers) {
        return request("HEAD", path, statusCode, headers);
    }
    public MockVirtualHost get(final String path, final int statusCode, final List<Header> headers) {
        return request("GET", path, statusCode, headers);
    }
    public MockVirtualHost post(final String path, final int statusCode, final List<Header> headers) {
        return request("POST", path, statusCode, headers);
    }
    public MockVirtualHost put(final String path, final int statusCode, final List<Header> headers) {
        return request("PUT", path, statusCode, headers);
    }
    public MockVirtualHost delete(final String path, final int statusCode, final List<Header> headers) {
        return request("DELETE", path, statusCode, headers);
    }
    public MockVirtualHost request(final String method, final String path, final int statusCode, final List<Header> headers) {
        return request(method, path, statusCode, headers, "");
    }

    public MockVirtualHost head(final String path, final int statusCode, final List<Header> headers, final Object body) {
        return request("HEAD", path, statusCode, headers, body);
    }
    public MockVirtualHost get(final String path, final int statusCode, final List<Header> headers, final Object body) {
        return request("GET", path, statusCode, headers, body);
    }
    public MockVirtualHost post(final String path, final int statusCode, final List<Header> headers, final Object body) {
        return request("POST", path, statusCode, headers, body);
    }
    public MockVirtualHost put(final String path, final int statusCode, final List<Header> headers, final Object body) {
        return request("PUT", path, statusCode, headers, body);
    }
    public MockVirtualHost delete(final String path, final int statusCode, final List<Header> headers, final Object body) {
        return request("DELETE", path, statusCode, headers, body);
    }
    public MockVirtualHost request(final String method, final String path, final int statusCode, final List<Header> headers, final Object body) {
        return request(method, path, statusCode, headers, gson.toJson(body));
    }

    public MockVirtualHost head(final String path, final int statusCode, final List<Header> headers, final String body) {
        return request("HEAD", path, statusCode, headers, body);
    }
    public MockVirtualHost get(final String path, final int statusCode, final List<Header> headers, final String body) {
        return request("GET", path, statusCode, headers, body);
    }
    public MockVirtualHost post(final String path, final int statusCode, final List<Header> headers, final String body) {
        return request("POST", path, statusCode, headers, body);
    }
    public MockVirtualHost put(final String path, final int statusCode, final List<Header> headers, final String body) {
        return request("PUT", path, statusCode, headers, body);
    }
    public MockVirtualHost delete(final String path, final int statusCode, final List<Header> headers, final String body) {
        return request("DELETE", path, statusCode, headers, body);
    }
    public MockVirtualHost request(final String method, final String path, final int statusCode, final List<Header> headers, final String body) {
        requests.add(new Request(new Endpoint(method, path), statusCode, headers, body));
        return this;
    }

    List<Request> toRequestsList() {
        return requests;
    }
}