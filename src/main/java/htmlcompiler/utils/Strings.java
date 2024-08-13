package htmlcompiler.utils;

public enum Strings {;

    public static boolean isNullOrEmpty(final String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNullOrBlank(final String value) {
        return value == null || value.isBlank();
    }

    public static boolean hasUppercase(final String value) {
        for (final char c : value.toCharArray()) {
            if (Character.isUpperCase(c))
                return true;
        }
        return false;
    }

}
