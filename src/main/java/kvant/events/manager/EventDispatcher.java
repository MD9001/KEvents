package kvant.events.manager;

import kvant.events.event.Event;
import kvant.events.event.ValueEvent;
import kvant.events.exception.EventFireException;
import kvant.events.handler.Handler;
import kvant.events.listener.Listener;
import kvant.events.manager.event.EventErrorEvent;
import kvant.events.model.EventResult;
import kvant.events.model.ValueList;

import java.time.Instant;
import java.util.Collection;

public interface EventDispatcher {
    boolean throwOnFail();

    void registerListener(Listener listener);

    void removeListener(Listener listener);

    void scheduleEvent(Event event, Instant time, Object... args);

    Collection<Handler> getHandlers(Event event, Object... args);

    default void scheduleEvent(Event event, long delay, Object... args) {
        var callTime = Instant.now().plusMillis(delay);

        scheduleEvent(event, callTime, args);
    }

    default void fire(Event event, Object... args) {
        for (Handler handler : getHandlers(event, args)) {
            if (event.isCancelled() && !handler.ignoreCancelled()) {
                continue;
            }

            try {
                handler.execute();
            } catch (Exception e) {
                if (throwOnFail())
                    throw new EventFireException(e);
                else
                    fire(new EventErrorEvent(event, e));
            }
        }
    }

    default <T> ValueList<T> call(ValueEvent<T> event, Object... args) {
        var values = new ValueList<T>();

        for (Handler handler : getHandlers(event, args)) {
            if (event.isCancelled() && !handler.ignoreCancelled()) {
                continue;
            }

            var value = (EventResult<T>) handler.executeForValue();

            values.add(value);
        }

        return values;
    }
}
