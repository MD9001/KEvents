package kvant.events.ticker;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public interface Ticker extends Closeable {
    Ticker until(Supplier<Boolean> until);

    Ticker step(long step);

    Ticker startDelay(long delay);

    Ticker executor(ExecutorService executor);

    void run(Runnable runnable);
}
