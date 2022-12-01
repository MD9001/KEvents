package kvant.events.model;

/**
 * The result of current event call
 * @param <T> - value parameter
 */

public class EventResult<T> {
    private final T result;
    private final Exception exception;

    public EventResult(T result, Exception exception) {
        this.result = result;
        this.exception = exception;
    }

    public boolean hasException() {
        return exception != null;
    }

    public T getValue() {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
