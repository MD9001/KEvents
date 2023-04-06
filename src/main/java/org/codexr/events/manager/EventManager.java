package org.codexr.events.manager;

import org.codexr.events.event.EventListener;
import org.codexr.events.event.EventObject;
import org.codexr.events.handler.Handler;
import org.codexr.events.marker.Event;
import org.codexr.events.marker.Listener;
import org.codexr.events.model.CallResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for calling and scheduling events.
 * After you created the listeners, you should register them
 * by calling registerListener(s) function.
 *
 * @see EventDispatcher#call(Event, Object...)
 */

public class EventManager implements EventDispatcher {
    protected final List<EventListener> listeners = new ArrayList<>();
    protected ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public List<Handler> getHandlers(EventObject event, Object... args) {
        var arguments = new Object[args.length + 1];
        arguments[0] = event.getEvent();

        System.arraycopy(args, 0, arguments, 1, args.length);

        var handlers = new ArrayList<Handler>();

        for (var listener : listeners) {
            handlers.addAll(listener.getMethodHandlers(event, arguments));
        }

        handlers.sort(null);

        return handlers;
    }

    public CompletableFuture<CallResult> callAsync(Event event, Object... args) {
        return CompletableFuture.supplyAsync(() -> call(event, args), executor);
    }

    @Override
    public void registerListener(Listener listener) {
        if (listener == null)
            return;

        listeners.add(new EventListener(listener));
    }

    @Override
    public void removeListener(Listener listener) {
        if (listener == null)
            return;

        listeners.removeIf(e -> e.getName().equals(listener.getClass().getTypeName()));
    }

    public void removeListeners() {
        listeners.clear();
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void close() {
        listeners.clear();
        executor.shutdown();
    }
}
