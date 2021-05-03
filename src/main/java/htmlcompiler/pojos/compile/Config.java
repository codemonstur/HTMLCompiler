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

public final class Config {

    public final Set<String> ignoreTags;
    public final Set<String> ignoreAttributes;
    public final Set<String> ignoreInputTypes;
    public final Map<String, Boolean> checks;
    public final boolean ignoreMajorVersions;
    public final ValidatorConfig validator;

    public Config() {
        this(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashMap<>(), false, new ValidatorConfig());
    }

    public Config(final Set<String> ignoreTags, final Set<String> ignoreAttributes,
                  final Set<String> ignoreInputTypes, final Map<String, Boolean> checks,
                  final boolean ignoreMajorVersions, final ValidatorConfig validator) {
        this.ignoreTags = ignoreTags;
        this.ignoreAttributes = ignoreAttributes;
        this.ignoreInputTypes = ignoreInputTypes;
        this.checks = checks;
        this.ignoreMajorVersions = ignoreMajorVersions;
        this.validator = validator;
    }

    public static Config readChecksConfiguration(final String confLocation) throws IOException {
        if (confLocation.isBlank()) return new Config();
        final Path confFile = Paths.get(confLocation);
        if (!isRegularFile(confFile)) return new Config();

        try (final Reader in = newBufferedReader(confFile)) {
            return GSON.fromJson(in, Config.class);
        }
    }

}
