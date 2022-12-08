package kvant.events.manager.impl;

import kvant.events.handler.annotation.EventHandler;
import kvant.events.event.Event;
import kvant.events.event.ValueEvent;
import kvant.events.event.VoidEvent;
import kvant.events.handler.Handler;
import kvant.events.handler.MethodHandler;
import kvant.events.listener.Listener;
import kvant.events.manager.EventDispatcher;
import kvant.events.manager.event.DelayedValueEvent;
import kvant.events.model.ScheduledEventData;
import kvant.events.model.ValueList;
import kvant.events.ticker.AsyncTicker;
import kvant.events.ticker.Ticker;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Class for calling, firing and scheduling events.
 *
 * @see ValueEvent
 * @see VoidEvent
 *
 * After you created the listeners, you should register them
 * by callig registerListener(s) function.
 * ANY event can be fired, but only ValueEvent can be called.
 * 
 * @see EventDispatcher#fire(Event, Object...)
 * @see EventDispatcher#call(ValueEvent, Object...)
 */

public class EventManager implements EventDispatcher, Closeable {
    private boolean throwOnFail = false;

    private ExecutorService executor = Executors.newCachedThreadPool();

    private final List<Listener> listeners = new ArrayList<>();
    private final Map<Event, Map<Listener, List<Method>>> cache = new HashMap<>();

    private Ticker ticker = new AsyncTicker();
    private final Map<Event, ScheduledEventData> scheduledEvents = new ConcurrentHashMap<>();

    @Override
    public boolean throwOnFail() {
        return throwOnFail;
    }

    @Override
    public TreeSet<Handler> getHandlers(Event event, Object... args) {
        var eventMethods = cache.get(event);

        if (eventMethods == null)
            eventMethods = findMethods(event);

        var arguments = new Object[args.length + 1];
        arguments[0] = event;

        System.arraycopy(args, 0, arguments, 1, args.length);

        var handlers = new TreeSet<Handler>();

        eventMethods.forEach(((listener, methods) -> {
            var applicableMethods = methods.stream()
                    .filter(method -> isApplicable(method, event, arguments))
                    .map(method -> createMethodHandler(listener, method, arguments))
                    .collect(Collectors.toList());

            handlers.addAll(applicableMethods);
        }));

        return handlers;
    }

    @Override
    public void scheduleEvent(Event event, Instant time, Object... args) {
        scheduledEvents.put(event, new ScheduledEventData(time, args));
    }

    public <T> CompletableFuture<ValueList<T>> callAsync(ValueEvent<T> event, Object... args) {
        return CompletableFuture.supplyAsync(() -> call(event, args), executor);
    }

    public CompletableFuture<Void> fireAsync(Event event, Object... args) {
        return CompletableFuture.runAsync(() -> fire(event, args), executor);
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void removeListeners() {
        listeners.clear();
    }

    public void registerListeners(List<Listener> listeners) {
        this.listeners.addAll(listeners);
    }

    private Map<Listener, List<Method>> findMethods(Event event) {
        var methods = new HashMap<Listener, List<Method>>();

        for (Listener listener : listeners) {
            var classMethods = Arrays.stream(listener.getClass().getDeclaredMethods())
                    .filter(m -> isEventHandler(m, event))
                    .peek(m -> m.setAccessible(true))
                    .collect(Collectors.toList());

            methods.put(listener, classMethods);
        }

        cache.put(event, methods);

        return methods;
    }

    private boolean isApplicable(Method method, Event event, Object[] args) {
        var typeParams = method.getParameterTypes();

        if (typeParams.length != args.length) return false;

        for (int i = 0; i < args.length; i++) {
            var typeParam = typeParams[i].getTypeName();
            var argType = args[i].getClass().getTypeName();

            if (!typeParam.equals(argType)) {
                return false;
            }
        }

        var returnType = method.getReturnType().getTypeName();

        return returnType.equals(event.getReturnTypeName());
    }

    private boolean isEventHandler(Method method, Event event) {
        if (!method.isAnnotationPresent(EventHandler.class)) return false;

        var typeParams = method.getParameterTypes();

        return typeParams.length != 0 && typeParams[0].getTypeName()
                .equals(event.typeName());
    }

    private Handler createMethodHandler(Listener listener, Method method, Object[] args) {
        var annotation = method.getAnnotation(EventHandler.class);

        return new MethodHandler(annotation.priority(), annotation.ignoreCancelled(),
                listener, method, args);
    }
    public void handleTicker() {
        Runnable task = () -> scheduledEvents.forEach((event, data) -> {
            if (Instant.now().compareTo(data.getFireTime()) < 0) return;

            scheduledEvents.remove(event);

            if (event instanceof ValueEvent) {
                var valueEvent = (ValueEvent) event;
                var value = call(valueEvent, data.getArgs());

                var delayedValueEvent = new DelayedValueEvent(valueEvent, value.getValues());
                fire(delayedValueEvent);
            } else {
                fire(event, data.getArgs());
            }
        });

        ticker.until(listeners::isEmpty)
                .step(100L)
                .run(task);
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }

    public void setThrowOnFail(boolean throwOnFail) {
        this.throwOnFail = throwOnFail;
    }

    @Override
    public void close() {
        listeners.clear();
        executor.shutdown();
    }
}
