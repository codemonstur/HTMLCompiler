package htmlcompiler.pojos.compile;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static htmlcompiler.utils.Json.GSON;
import static htmlcompiler.utils.Strings.isNullOrBlank;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newBufferedReader;

public final class CompilerConfig {

    public final Set<String> ignoreTags;
    public final Set<String> ignoreAttributes;
    public final Set<String> ignoreInputTypes;
    public final Map<String, Boolean> checks;
    public final boolean ignoreMajorVersions;
    public final ValidatorConfig validator;

    public CompilerConfig() {
        this(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashMap<>(), false, new ValidatorConfig());
    }

    public CompilerConfig(final Set<String> ignoreTags, final Set<String> ignoreAttributes,
                          final Set<String> ignoreInputTypes, final Map<String, Boolean> checks,
                          final boolean ignoreMajorVersions, final ValidatorConfig validator) {
        this.ignoreTags = ignoreTags;
        this.ignoreAttributes = ignoreAttributes;
        this.ignoreInputTypes = ignoreInputTypes;
        this.checks = checks;
        this.ignoreMajorVersions = ignoreMajorVersions;
        this.validator = validator;
    }

    private static final Type CONFIG_MAP_TYPE = new TypeToken<Map<String, CompilerConfig>>() {}.getType();
    public static Map<String, CompilerConfig> readChecksConfiguration(final String confLocation) throws IOException {
        if (isNullOrBlank(confLocation)) return Map.of("", new CompilerConfig());
        final Path confFile = Paths.get(confLocation);
        if (!isRegularFile(confFile)) return Map.of("", new CompilerConfig());

        try (final Reader in = newBufferedReader(confFile)) {
            return GSON.fromJson(in, CONFIG_MAP_TYPE);
        }
    }

}
