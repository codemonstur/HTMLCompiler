package htmlcompiler.pojos.compile;

import java.nio.file.Path;

import static htmlcompiler.utils.Filenames.toExtension;

public enum ImageType {
    bmp("image/bmp"),
    cmx("image/x-cmx"),
    cod("image/cis-cod"),
    gif("image/gif"),
    ico("image/x-icon"),
    ief("image/ief"),
    jfif("image/pipeg"),
    jpe("image/jpeg"),
    jpeg("image/jpeg"),
    jpg("image/jpeg"),
    pbm("image/x-portable-bitmap"),
    pgm("image/x-portable-graymap"),
    pnm("image/x-portable-anymap"),
    png("image/png"),
    ppm("image/x-portable-pixmap"),
    ras("image/x-cmu-raster"),
    rgb("image/x-rgb"),
    tif("image/tiff"),
    tiff("image/tiff"),
    xbm("image/x-xbitmap"),
    xpm("image/x-xpixmap"),
    xwd("image/x-xwindowdump");

    public final String mimetype;
    ImageType(final String mimetype) {
        this.mimetype = mimetype;
    }

    public static boolean isBinaryImage(final String filename) {
        try {
            ImageType.valueOf(toExtension(filename));
            return true;
        }catch (Exception e) {
            return false;
        }
    }
    public static boolean isBinaryImage(final Path file) {
        return isBinaryImage(file.getFileName().toString());
    }
    public static String toMimeType(final String filename) {
        return ImageType.valueOf(toExtension(filename)).mimetype;
    }
    public static String toMimeType(final Path filename) {
        return ImageType.valueOf(toExtension(filename)).mimetype;
    }

}
