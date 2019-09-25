package util;

public class LoopingSingleThread implements Service {

    private Thread thread;
    private boolean running = true;

    public LoopingSingleThread(final InterruptableTask runnable) {
        this.thread = new Thread(() -> {
            while (running && !Thread.interrupted()) {
                try {
                    runnable.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            running = false;
        });
    }

    @Override
    public Service start() {
        thread.start();
        return this;
    }

    @Override
    public Service join() throws InterruptedException {
        thread.join();
        return this;
    }

    @Override
    public Service stop() {
        running = false;
        return this;
    }
}
