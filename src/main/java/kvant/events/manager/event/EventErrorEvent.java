package kvant.events.manager.event;

import kvant.events.event.Event;
import kvant.events.event.VoidEvent;

/**
 * Event fired by EventManager when calling event was unsuccessful.
 * NOTE: Called only when EventManager configured on not throwing on error.
 *
 * @see kvant.events.manager.EventManager
 */
public class EventErrorEvent extends VoidEvent {
    private final Event event;
    private final Exception exception;

    public EventErrorEvent(Event event, Exception exception) {
        this.event = event;
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public Event getEvent() {
        return event;
    }
}
