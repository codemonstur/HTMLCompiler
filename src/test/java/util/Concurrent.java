package util;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;

public enum Concurrent {;

    public interface CheckedRunnable {
        void run() throws Exception;
    }

    public static Runnable newProgressTracker() {
        final var checked = new Counter();
        return () -> {
            synchronized (checked) {
                checked.increment();
                System.out.print(".");
                if (checked.get() % 100 == 0) System.out.print("\n");
            }
        };
    }

    public static Runnable newTaskWithProgress(final Runnable progress, final CheckedRunnable task) {
        return () -> {
            try {
                progress.run();
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public interface TaskRunner<T> {
        CheckedRunnable newTaskRunner(T task);
    }

    public static <T> void executeMultiThreaded(final int numThreads, final Set<T> tasks, final TaskRunner<T> supplier) {
        final ExecutorService threads = Executors.newFixedThreadPool(numThreads);

        final var tracker = newProgressTracker();
        for (final var task : tasks) {
            threads.submit(newTaskWithProgress(tracker, supplier.newTaskRunner(task)));
        }

        shutdownAndWait(threads);
    }

    public static void shutdownAndWait(final ExecutorService executorService) {
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                executorService.awaitTermination(10, SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
