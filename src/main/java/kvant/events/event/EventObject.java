package kvant.events.event;

import kvant.events.annotations.Event;
import kvant.events.enums.TypeName;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EventObject {
    protected final Object object;
    protected boolean cancelled = false;

    protected final String eventName;
    protected final Set<String> typeNames;

    public EventObject(Object object) {
        this.object = object;

        var annotation = object.getClass().getAnnotation(Event.class);

        if (annotation == null) {
            throw new IllegalArgumentException("Try to call non-event object.");
        }

        if (object instanceof Cancellable) {
            cancelled = ((Cancellable) object).isCancelled();
        }

        eventName = object.getClass().getTypeName();

        typeNames = Arrays.stream(annotation.typeNames())
                .map(TypeName::getTypeName)
                .collect(Collectors.toSet());
    }

    public Set<String> getTypeNames() {
        return typeNames;
    }

    public Object getObject() {
        return object;
    }

    public String getEventName() {
        return eventName;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
