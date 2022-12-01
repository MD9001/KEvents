package kvant.events.ticker;

import java.io.Closeable;

public interface Ticker extends Closeable {
    void run(Runnable runnable);
}
