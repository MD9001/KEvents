package kvant.events.event;

/**
 * The interface, all current event types inherit from.
 */

public interface Event {
    String getReturnType();

    default String typeName() {
        return getClass().getTypeName();
    }

    default String name() {
        return getClass().getSimpleName();
    }
}
