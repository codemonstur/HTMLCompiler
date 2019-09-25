package util;

public interface Service {
    Service start();
    Service join() throws InterruptedException;
    Service stop();
}