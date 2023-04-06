package org.codexr.events.event;

import org.codexr.events.annotations.TypeNames;
import org.codexr.events.enums.PrimitiveTypes;
import org.codexr.events.marker.Event;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EventObject {
    protected final Event event;
    protected final String eventName;
    protected final Set<String> typeNames;
    protected boolean cancelled = false;

    public EventObject(Event event) {
        this.event = event;

        var annotation = event.getClass().getAnnotation(TypeNames.class);

        Class<?>[] types = (annotation == null) ? new Class<?>[]{Void.class} : annotation.typeNames();

        if (event instanceof Cancellable) {
            cancelled = ((Cancellable) event).isCancelled();
        }

        eventName = event.getClass().getTypeName();

        typeNames = Arrays.stream(types)
                .map(PrimitiveTypes::getTypeName)
                .collect(Collectors.toSet());
    }

    public Set<String> getTypeNames() {
        return typeNames;
    }

    public Event getEvent() {
        return event;
    }

    public String getEventName() {
        return eventName;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
