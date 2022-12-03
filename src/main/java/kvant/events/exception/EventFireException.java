package kvant.events.exception;

import kvant.events.manager.impl.EventManager;

/**
 * Exception thrown when event was fired unsuccessfully.
 * NOTE: Exception is thrown only when EventManager is configured
 * to throw exceptions on fail.
 *
 * @see EventManager
 */

public class EventFireException extends RuntimeException {
    public EventFireException(Exception e) {
        super(e);
    }
}
