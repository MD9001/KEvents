package kvant.events.event;

/**
 * Specifies if your event is cancellable or not.
 */
public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
