package htmlcompiler.services;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static htmlcompiler.tools.Coding.encodeBase64;
import static htmlcompiler.tools.Coding.sha384;
import static htmlcompiler.tools.HTTP.urlHasCorsAllowed;
import static htmlcompiler.tools.HTTP.urlToByteArray;
import static htmlcompiler.tools.Json.GSON;
import static java.nio.file.Files.*;

public enum Repository {;

    private static Path locationCachedIntegrityValues;
    private static Map<String, String> cachedIntegrityValues;

    public static String uriToIntegrityValue(final String uri) throws IOException, NoSuchAlgorithmException {
        if (cachedIntegrityValues == null) {
            locationCachedIntegrityValues = getRepositoryDirectory().resolve("uri-to-integrity.json");
            cachedIntegrityValues = readHashMap(locationCachedIntegrityValues);
        }
        String integrity = cachedIntegrityValues.get(uri);
        if (integrity != null) return integrity;

        if (!urlHasCorsAllowed(uri))
            throw new IOException("URI " + uri + " does not have * in Access-Control-Allow-Origin header. Consider loading this resource from a different URI or adding the 'no-integrity' attribute to the tag");

        integrity = toIntegrityValue(urlToByteArray(uri));
        cachedIntegrityValues.put(uri, integrity);
        writeHashMap(cachedIntegrityValues, locationCachedIntegrityValues);

        return integrity;
    }

    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();
    private static Map<String, String> readHashMap(final Path jsonFile) throws IOException {
        if (!exists(jsonFile)) return new HashMap<>();

        try (final var reader = newBufferedReader(jsonFile)) {
            return GSON.fromJson(reader, MAP_TYPE);
        }
    }
    private static void writeHashMap(final Map<String, String> map, final Path destination) throws IOException {
        try (final var writer = newBufferedWriter(destination)) {
            GSON.toJson(map, MAP_TYPE, GSON.newJsonWriter(writer));
        }
    }

    private static Path getRepositoryDirectory() throws IOException {
        final Path repo = Paths.get(System.getProperty("user.home")).resolve(".htmlcompiler");
        if (!exists(repo)) createDirectories(repo);
        if (!isDirectory(repo)) throw new IOException("'~/.htmlcompiler' is not a directory");
        return repo;
    }

    private static String toIntegrityValue(final byte[] data) throws NoSuchAlgorithmException {
        return "sha384-"+encodeBase64(sha384(data));
    }

}
