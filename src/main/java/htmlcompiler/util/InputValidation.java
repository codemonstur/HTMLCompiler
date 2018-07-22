package htmlcompiler.util;

public enum InputValidation {;

    public static <T> T orDefault(final T value, T _default) {
        return (value != null) ? value : _default;
    }

}
