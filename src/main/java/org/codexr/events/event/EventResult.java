package org.codexr.events.event;

import java.util.Objects;

public class EventResult {
    protected final EventObject event;

    protected Object value = null;
    protected Exception exception = null;

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
