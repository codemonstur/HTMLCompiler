package htmlcompiler.checks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static java.nio.file.Files.isRegularFile;

public enum ReadCheckConfiguration {;

    public static Map<String, Boolean> readChecksConfiguration(final String confLocation, final Gson gson) throws IOException {
        if (confLocation.isBlank()) return Collections.emptyMap();
        final Path confFile = Paths.get(confLocation);
        if (!isRegularFile(confFile)) return Collections.emptyMap();

        final String data = Files.readString(confFile);
        return gson.fromJson(data, new TypeToken<Map<String, Boolean>>() {}.getType());
    }

}
