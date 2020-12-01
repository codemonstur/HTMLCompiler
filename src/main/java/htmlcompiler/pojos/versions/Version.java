package htmlcompiler.pojos.versions;

import static xmlparser.utils.Functions.isNullOrEmpty;

public final class Version {
    public final int major;
    public final int minor;
    public final int patch;
    public final String original;

    public Version(final String version) {
        this(version, version.split("\\."));
    }
    public Version(final String original, final String[] version) {
        this.original = original;
        this.major = version.length > 0 ? parseInt(version[0], -1) : -1;
        this.minor = version.length > 1 ? parseInt(version[1], -1) : -1;
        this.patch = version.length > 2 ? parseInt(version[2], -1) : -1;
    }
    private static int parseInt(final String value, final int defaultValue) {
        if (isNullOrEmpty(value)) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean isNewerThan(final Version version) {
        return (major >  version.major)
            || (major == version.major
            && (minor >  version.minor
            || (minor == version.minor
            && (patch >  version.patch))));
    }
}
