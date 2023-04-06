package org.codexr.events.manager;

import org.codexr.events.event.EventObject;
import org.codexr.events.event.EventResult;
import org.codexr.events.handler.Handler;
import org.codexr.events.marker.Event;
import org.codexr.events.marker.Listener;
import org.codexr.events.model.CallResult;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public interface EventDispatcher extends Closeable {
    void registerListener(Listener listener);

    void removeListener(Listener listener);

    Collection<Handler> getHandlers(EventObject event, Object... args);

    default CallResult call(Event event, Object... args) {
        var eventObject = new EventObject(event);

        var results = new ArrayList<EventResult>();

        for (Handler handler : getHandlers(eventObject, args)) {
            if (eventObject.isCancelled() && !handler.ignoreCancelled()) {
                continue;
            }

            EventResult result;

            try {
                result = new EventResult(eventObject, handler.execute());
            } catch (Exception e) {
                result = new EventResult(eventObject, e);
            }

            results.add(result);
        }

        return new CallResult(eventObject, results);
    }

    default CompletableFuture<CallResult> callAsync(Event event, Object... args) {
        return CompletableFuture.supplyAsync(() -> call(event, args), getExecutor());
    }

    default CompletableFuture<CallResult> scheduleEvent(Event event, long delay, Object... args) {
        var delayedExec = CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS, getExecutor());

        return CompletableFuture.supplyAsync(() -> call(event, args), delayedExec);
    }

    ExecutorService getExecutor();

    @Override
    default void close() {
        getExecutor().shutdown();
    }
}
