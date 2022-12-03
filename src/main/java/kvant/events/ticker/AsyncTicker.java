package kvant.events.ticker;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Simple ticker class for running tasks until specified condition.
 */
public class AsyncTicker implements Ticker {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private final AtomicBoolean stop = new AtomicBoolean(false);
    private Supplier<Boolean> until = () -> false;

    private long stepMillis = 100L;
    private long startDelay = 0L;

    @Override
    public AsyncTicker until(Supplier<Boolean> until) {
        this.until = until;
        return this;
    }

    @Override
    public AsyncTicker step(long step) {
        this.stepMillis = step;
        return this;
    }

    @Override
    public AsyncTicker startDelay(long delay) {
        this.startDelay = delay;
        return this;
    }

    @Override
    public AsyncTicker executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public void run(Runnable runnable) {
        CompletableFuture.runAsync(() -> {
            trySleep(startDelay);

            while (!until.get() && !stop.get()) {
                runnable.run();
                trySleep(stepMillis);
            }
        }, executor);

    }

    @Override
    public void close() {
        stop.set(true);
    }

    private static void trySleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
