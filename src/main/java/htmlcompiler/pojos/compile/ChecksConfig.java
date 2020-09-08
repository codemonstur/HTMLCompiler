package htmlcompiler.pojos.compile;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static htmlcompiler.tools.Json.GSON;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newBufferedReader;
import static java.util.Collections.emptySet;

public final class ChecksConfig {

    public final Set<String> ignoreTags;
    public final Set<String> ignoreAttributes;
    public final Map<String, Boolean> checks;

    private ChecksConfig() {
        this.ignoreTags = new HashSet<>();
        this.ignoreAttributes = new HashSet<>();
        this.checks = new HashMap<>();
    }

    public ChecksConfig(final Set<String> ignoreTags, final Set<String> ignoreAttributes, final Map<String, Boolean> checks) {
        this.ignoreTags = ignoreTags;
        this.ignoreAttributes = ignoreAttributes;
        this.checks = checks;
    }

    public static ChecksConfig readChecksConfiguration(final String confLocation) throws IOException {
        if (confLocation.isBlank()) return new ChecksConfig(emptySet(), emptySet(), new HashMap<>());
        final Path confFile = Paths.get(confLocation);
        if (!isRegularFile(confFile)) return new ChecksConfig(emptySet(), emptySet(), new HashMap<>());

        try (final Reader in = newBufferedReader(confFile)) {
            return GSON.fromJson(in, ChecksConfig.class);
        }
    }

}
