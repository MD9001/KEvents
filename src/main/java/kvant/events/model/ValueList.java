package kvant.events.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Return value when Event extending ValueEvent is called.
 * @see kvant.events.event.ValueEvent
 *
 * By default, all ValueEvent return ValueList, because events
 * may have several eventhandler methods with same specified return type.
 * If you have only one method for your ValueEvent annotated by EventHandler annotation,
 * you can call simple single() method, to get value.
 *
 * @see EventResult
 */

public class ValueList<T> {
    private final List<EventResult<T>> values;

    public ValueList(List<EventResult<T>> values) {
        this.values = values;
    }

    public ValueList() {
        this(new ArrayList<>());
    }

    public List<EventResult<T>> getValues() {
        return values;
    }

    public void add(EventResult<T> value) {
        values.add(value);
    }

    public EventResult<T> single() {
        return values.stream().findFirst().orElse(null);
    }
}
