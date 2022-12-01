package kvant.events.exception;

/**
 * Exception thrown when event was fired unsuccessfully.
 * NOTE: Exception is thrown only when EventManager is configured
 * to throw exceptions on fail.
 *
 * @see kvant.events.manager.EventManager
 */

public class EventFireException extends RuntimeException {
    public EventFireException(Exception e) {
        super(e);
    }
}
