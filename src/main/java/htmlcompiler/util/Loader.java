package htmlcompiler.util;

import htmlcompiler.error.InvalidInput;

import java.io.*;

import static htmlcompiler.util.HTTP.isUrl;
import static htmlcompiler.util.HTTP.urlToInputStream;
import static htmlcompiler.util.IO.relativize;
import static htmlcompiler.util.IO.toByteArray;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public final class Loader {

    private final String root;
    public Loader(final String root) {
        this.root = requireNonNull(root, "Root must be set");
    }

    public String getRoot() {
        return root;
    }

    public String relative(final String location) {
        return relativize(root, location);
    }

    public byte[] getAsBytes(final String location) throws IOException {
        try (final InputStream in = getInputStream(location)) {
            return toByteArray(in);
        }
    }

    public String getAsString(final String location) throws IOException {
        try (final InputStream in = getInputStream(location)) {
            return IO.toString(in);
        }
    }

    private InputStream getInputStream(final String location) throws IOException {
        if (isUrl(location))
            return urlToInputStream(location);
        if (isClasspathResource(root+location))
            return Loader.class.getResourceAsStream(root+location);
        if (new File(root, location).isFile())
            return new FileInputStream(new File(root, location));
        throw new FileNotFoundException(root+location);
    }

    public File toLocation(final File origin, final String link, final String message) throws InvalidInput {
        if (link.startsWith("/")) return new File(root, link);
        final File location = new File((origin.isDirectory()) ? origin : origin.getParentFile(), link);
        if (!location.exists())
            throw new InvalidInput(format(message, origin.getPath(), link));
        return location;
    }

    private static boolean isClasspathResource(final String resource) {
        return Loader.class.getResource(resource) != null;
    }

}
