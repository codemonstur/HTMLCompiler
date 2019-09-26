package htmlcompiler.services;

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
    public LoopingSingleThread start() {
        if (!running) throw new IllegalStateException();
        thread.start();
        return this;
    }

    @Override
    public LoopingSingleThread waitUntilDone() throws InterruptedException {
        thread.join();
        return this;
    }

    @Override
    public LoopingSingleThread stop() {
        running = false;
        return this;
    }
}
