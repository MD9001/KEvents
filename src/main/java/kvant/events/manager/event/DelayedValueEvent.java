package kvant.events.manager.event;

import kvant.events.event.ValueEvent;
import kvant.events.event.VoidEvent;
import kvant.events.manager.impl.EventManager;
import kvant.events.model.EventResult;
import kvant.events.model.ValueList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event fired by EventManager's ticker after the scheduled ValueEvent is called.
 * Can be processed by declaring EventHandler method in your listener class with
 * DelayedValueEvent as an argument
 *
 * @see EventManager
 * @see ValueEvent
 */

public class DelayedValueEvent extends VoidEvent {
    private final ValueEvent<?> event;
    private final List<EventResult<?>> values;

    public DelayedValueEvent(ValueEvent<?> event, List<EventResult<?>> values) {
        this.event = event;
        this.values = values;
    }

    public ValueEvent<?> getEvent() {
        return event;
    }

    public List<EventResult<?>> getValues() {
        return values;
    }

    public <T> ValueList<T> forClass(Class<T> clazz) {
        return new ValueList<>(values.stream().map(e -> {
            if (e.hasException()) {
                return new EventResult<>(clazz.cast(null), e.getException());
            }

            return new EventResult<>(clazz.cast(e.getValue()), null);
        }).collect(Collectors.toList()));
    }
}
