package htmlcompiler.services;

public interface InterruptableTask {
    void run() throws InterruptedException;
}
