package kvant.events.model;

import kvant.events.event.EventObject;
import kvant.events.event.EventResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallResult {
    private final EventObject eventObject;
    private final List<EventResult> values;

    public CallResult(EventObject eventObject, List<EventResult> values) {
        this.eventObject = eventObject;
        this.values = values;
    }

    public CallResult(EventObject eventObject) {
        this(eventObject, new ArrayList<>());
    }

    public List<EventResult> getEventResults() {
        return values;
    }

    public <T> List<T> getValues(Class<T> clazz) {
        return values.stream()
                .filter(r -> clazz.isInstance(r.getValue()))
                .map(r -> clazz.cast(r.getValue()))
                .collect(Collectors.toList());
    }

    public void add(EventResult value) {
        values.add(value);
    }

    public EventResult first() {
        return values.stream().findFirst().orElse(null);
    }

    public <T> T first(Class<T> clazz) {
        var firstValue = values.stream()
                .filter(e -> clazz.isInstance(e.getValue()))
                .findFirst()
                .orElse(null);

        if (firstValue == null) return null;

        return firstValue.getValue(clazz);
    }

    public EventObject getEventObject() {
        return eventObject;
    }
}
