package kvant.events.event;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The class, all current event types inherit from.
 */

public abstract class Event {
    protected final AtomicBoolean cancelled = new AtomicBoolean(false);

    public abstract String getReturnTypeName();

    public String typeName() {
        return getClass().getTypeName();
    }

    public String name() {
        return getClass().getSimpleName();
    }

    public void setCancelled(boolean cancel) {
        cancelled.set(cancel);
    }

    public boolean isCancelled() {
        return cancelled.get();
    }
}
