package kvant.events.manager;

import kvant.events.event.EventListener;
import kvant.events.event.EventObject;
import kvant.events.handler.Handler;
import kvant.events.model.CallResult;

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
 * @see EventDispatcher#call(Object, Object...)
 */

public class EventManager implements EventDispatcher {
    protected ExecutorService executor = Executors.newCachedThreadPool();
    protected final List<EventListener> listeners = new ArrayList<>();

    @Override
    public List<Handler> getHandlers(EventObject event, Object... args) {
        var arguments = new Object[args.length + 1];
        arguments[0] = event.getObject();

        System.arraycopy(args, 0, arguments, 1, args.length);

        var handlers = new ArrayList<Handler>();

        for (var listener : listeners) {
            handlers.addAll(listener.getMethodHandlers(event, arguments));
        }

        handlers.sort(null);

        return handlers;
    }

    public CompletableFuture<CallResult> callAsync(Object event, Object... args) {
        return CompletableFuture.supplyAsync(() -> call(event, args), executor);
    }

    public void registerListener(Object listener) {
        listeners.add(new EventListener(listener));
    }

    @Override
    public void removeListener(Object listener) {
        if (listener == null) return;

        listeners.removeIf(e -> e.getName().equals(listener.getClass().getTypeName()));
    }

    public void removeListeners() {
        listeners.clear();
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void close() {
        listeners.clear();
        executor.shutdown();
    }
}
