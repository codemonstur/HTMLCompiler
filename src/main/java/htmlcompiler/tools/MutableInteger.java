package htmlcompiler.tools;

public final class MutableInteger {

    private int value = 0;

    public int getValue() {
        return value;
    }

    public void increment() {
        value++;
    }
}
