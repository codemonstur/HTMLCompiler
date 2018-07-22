package htmlcompiler.util;

import java.util.HashMap;
import java.util.Map;

public enum Extensions {;

    private static final Map<String, String> EXTENSION_TO_MIMETYPE = new HashMap<>();
    static {
        EXTENSION_TO_MIMETYPE.put("bmp","image/bmp");
        EXTENSION_TO_MIMETYPE.put("cmx","image/x-cmx");
        EXTENSION_TO_MIMETYPE.put("cod","image/cis-cod");
        EXTENSION_TO_MIMETYPE.put("gif","image/gif");
        EXTENSION_TO_MIMETYPE.put("ico","image/x-icon");
        EXTENSION_TO_MIMETYPE.put("ief","image/ief");
        EXTENSION_TO_MIMETYPE.put("jfif","image/pipeg");
        EXTENSION_TO_MIMETYPE.put("jpe","image/jpeg");
        EXTENSION_TO_MIMETYPE.put("jpeg","image/jpeg");
        EXTENSION_TO_MIMETYPE.put("jpg","image/jpeg");
        EXTENSION_TO_MIMETYPE.put("pbm","image/x-portable-bitmap");
        EXTENSION_TO_MIMETYPE.put("pgm","image/x-portable-graymap");
        EXTENSION_TO_MIMETYPE.put("pnm","image/x-portable-anymap");
        EXTENSION_TO_MIMETYPE.put("png","image/png");
        EXTENSION_TO_MIMETYPE.put("ppm","image/x-portable-pixmap");
        EXTENSION_TO_MIMETYPE.put("ras","image/x-cmu-raster");
        EXTENSION_TO_MIMETYPE.put("rgb","image/x-rgb");
        EXTENSION_TO_MIMETYPE.put("tif","image/tiff");
        EXTENSION_TO_MIMETYPE.put("tiff","image/tiff");
        EXTENSION_TO_MIMETYPE.put("xbm","image/x-xbitmap");
        EXTENSION_TO_MIMETYPE.put("xpm","image/x-xpixmap");
        EXTENSION_TO_MIMETYPE.put("xwd","image/x-xwindowdump");
    }

    public static boolean isBinaryImage(final String location) {
        return EXTENSION_TO_MIMETYPE.containsKey(location.substring(location.lastIndexOf('.')+1));
    }
    public static String toMimeType(final String location) {
        return EXTENSION_TO_MIMETYPE.get(location.substring(location.lastIndexOf('.')+1));
    }
}
