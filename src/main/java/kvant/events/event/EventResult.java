package kvant.events.event;

import java.util.Objects;

public class EventResult {
    private final EventObject event;

    private Object value = null;
    private Exception exception = null;

    public EventResult(EventObject event) {
        Objects.requireNonNull(event);

        this.event = event;
    }

    public EventResult(EventObject event, Object value) {
        this(event);
        this.value = value;
    }

    public EventResult(EventObject event, Exception exception) {
        this(event);
        this.exception = exception;
    }

    public EventObject getEvent() {
        return event;
    }

    public Object getValue() {
        return value;
    }

    public <T> T getValue(Class<T> clazz) {
        return clazz.cast(value);
    }

    public Exception getException() {
        return exception;
    }

    public boolean isSuccess() {
        return exception == null;
    }
}
