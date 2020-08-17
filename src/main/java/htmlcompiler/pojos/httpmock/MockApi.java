package htmlcompiler.pojos.httpmock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MockApi {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, List<Request>> requests = new HashMap<>();

    public MockApi virtualHost(final String hostname, final MockVirtualHost mock) {
        requests.put(hostname, mock.toRequestsList());
        return this;
    }
    public MockApi defaultHost(final MockVirtualHost mock) {
        requests.put("", mock.toRequestsList());
        return this;
    }

    public String build() {
        return gson.toJson(requests);
    }

    public void print() {
        System.out.println(build());
    }

    public void writeToFile(final String path) throws IOException {
        Files.writeString(Paths.get(path), build());
    }

}
