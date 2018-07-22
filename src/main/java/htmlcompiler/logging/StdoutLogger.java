package htmlcompiler.logging;

public final class StdoutLogger implements Logger {
    @Override
    public void info(final String message) {
        System.out.println(message);
    }

    @Override
    public void warn(String message) {
        System.out.println(message);
    }
}
