package htmlcompiler.tools;

import htmlcompiler.model.error.InvalidInput;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public enum IO {;

    public static String toString(final File file) throws IOException {
        try (final InputStream in = new FileInputStream(file)) {
            return toString(in, UTF_8);
        }
    }
    public static String toString(final InputStream in) throws IOException {
        return toString(in, UTF_8);
    }
    public static String toString(final InputStream in, final Charset set) throws IOException {
        final StringBuilder out = new StringBuilder();
        byte[] buffer = new byte[1024]; int read;
        while ((read = in.read(buffer)) != -1) {
            out.append(new String(buffer, 0, read, set));
        }
        return out.toString();
    }

    public static byte[] toByteArray(final File file) throws IOException {
        try (final InputStream in = new FileInputStream(file)) {
            return toByteArray(in);
        }
    }
    public static byte[] toByteArray(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    public static File writeStringToFile(final String data, final File file) throws FileNotFoundException {
        try (final PrintWriter out = new PrintWriter(file)) {
            out.println(data);
        }
        return file;
    }

    public static String relativize(final String basedir, final String outputDir) {
        return Paths.get(basedir).relativize(Paths.get(outputDir)).toString();
    }

    public static String relativize(final File basedir, final File outputDir) {
        return basedir.toPath().relativize(outputDir.toPath()).toString();
    }

    public static File toLocation(final File origin, final String link, final String message) throws InvalidInput {
        final File location = new File((origin.isDirectory()) ? origin : origin.getParentFile(), link);
        if (!location.exists())
            throw new InvalidInput(format(message, origin.getPath(), link));
        return location;
    }
}
