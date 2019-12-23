package htmlcompiler.tools;

public enum Strings {;

    public static boolean hasUppercase(final String value) {
        for (final char c : value.toCharArray()) {
            if (Character.isUpperCase(c))
                return true;
        }
        return false;
    }

}
