package htmlcompiler.services;

public interface Service {
    Service start();
    Service waitUntilDone() throws InterruptedException;
    Service stop();
}