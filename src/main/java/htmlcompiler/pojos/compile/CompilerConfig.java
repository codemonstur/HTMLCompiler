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

public record CompilerConfig(
        Set<String> ignoreTags,
        Set<String> ignoreAttributes,
        Set<String> ignoreInputTypes,
        Map<String, Boolean> checks,
        boolean ignoreMajorVersions,
        ValidatorConfig validator) {

    public CompilerConfig() {
        this(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashMap<>(), false, new ValidatorConfig());
    }

    /** Components that the JSON file omits arrive as null in the canonical constructor, so restore the
     *  defaults that used to come from the no-arg constructor. */
    static CompilerConfig withDefaults(final CompilerConfig config) {
        if (config == null) return new CompilerConfig();
        return new CompilerConfig(
                config.ignoreTags() != null ? config.ignoreTags() : new HashSet<>(),
                config.ignoreAttributes() != null ? config.ignoreAttributes() : new HashSet<>(),
                config.ignoreInputTypes() != null ? config.ignoreInputTypes() : new HashSet<>(),
                config.checks() != null ? config.checks() : new HashMap<>(),
                config.ignoreMajorVersions(),
                ValidatorConfig.withDefaults(config.validator()));
    }

    private static final Type CONFIG_MAP_TYPE = new TypeToken<Map<String, CompilerConfig>>() {}.getType();
    public static Map<String, CompilerConfig> readChecksConfiguration(final String confLocation) throws IOException {
        if (isNullOrBlank(confLocation)) return Map.of("", new CompilerConfig());
        final Path confFile = Paths.get(confLocation);
        if (!isRegularFile(confFile)) return Map.of("", new CompilerConfig());

        try (final Reader in = newBufferedReader(confFile)) {
            final Map<String, CompilerConfig> configs = GSON.fromJson(in, CONFIG_MAP_TYPE);
            if (configs == null) return Map.of("", new CompilerConfig());
            configs.replaceAll((name, config) -> withDefaults(config));
            return configs;
        } catch (final RuntimeException e) {
            throw new IOException("Invalid checks configuration file " + confFile, e);
        }
    }

}
