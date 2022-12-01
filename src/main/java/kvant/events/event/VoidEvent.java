package kvant.events.event;

/**
 * Event, event handler of which doesn't have return statement with value.
 * If you need to return value from calling event,
 * @see ValueEvent
 */
public abstract class VoidEvent implements Event {
    @Override
    public String getReturnType() {
        return "void";
    }
}