package kvant.events.manager;

import kvant.events.event.Event;
import kvant.events.event.ValueEvent;
import kvant.events.exception.EventFireException;
import kvant.events.listener.Listener;
import kvant.events.manager.event.EventErrorEvent;
import kvant.events.model.EventResult;
import kvant.events.model.ValueList;
import kvant.events.handler.Handler;

import java.time.Instant;
import java.util.List;

public interface EventDispatcher {
    boolean throwOnFail();

    void registerListener(Listener listener);

    void scheduleEvent(Event event, Instant time, Object... args);

    List<Handler> getHandlers(Event event, Object... args);

    default void scheduleEvent(Event event, long delay, Object... args) {
        var callTime = Instant.now().plusMillis(delay);

        scheduleEvent(event, callTime, args);
    }

    default void fire(Event event, Object... args) {
        getHandlers(event, args).forEach(handler -> {
            try {
                handler.execute();
            } catch (Exception e) {
                if (throwOnFail())
                    throw new EventFireException(e);
                else
                    fire(new EventErrorEvent(event, e));
            }
        });
    }

    default <T> ValueList<T> call(ValueEvent<T> event, Object... args) {
        var values = new ValueList<T>();

        getHandlers(event, args).forEach(func -> {
            try {
                var value = (EventResult<T>) func.executeForValue();

                values.add(value);
            } catch (Exception e) {
                if (throwOnFail())
                    throw new EventFireException(e);
                else
                    fire(new EventErrorEvent(event, e));
            }
        });

        return values;
    }
}
