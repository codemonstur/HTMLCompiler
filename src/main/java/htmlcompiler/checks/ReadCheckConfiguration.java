package htmlcompiler.checks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

public enum ReadCheckConfiguration {;

    public static Map<String, Boolean> readChecksConfiguration(final String confLocation, final Gson gson) throws IOException {
        if (confLocation.isBlank()) return Collections.emptyMap();
        final File confFile = new File(confLocation);
        if (!confFile.exists()) return Collections.emptyMap();

        final String data = Files.readString(confFile.toPath());
        return gson.fromJson(data, new TypeToken<Map<String, Boolean>>() {}.getType());
    }

}
