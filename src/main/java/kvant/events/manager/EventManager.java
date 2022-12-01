package kvant.events.manager;

import kvant.events.annotation.EventHandler;
import kvant.events.event.Event;
import kvant.events.event.ValueEvent;
import kvant.events.event.VoidEvent;
import kvant.events.exception.EventFireException;
import kvant.events.listener.Listener;
import kvant.events.manager.event.DelayedValueEvent;
import kvant.events.manager.event.EventErrorEvent;
import kvant.events.model.EventResult;
import kvant.events.model.JavaFunction;
import kvant.events.model.ScheduledEventData;
import kvant.events.model.ValueList;
import kvant.events.ticker.AsyncTicker;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
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
 * @see EventManager#fire(Event, Object...)
 * @see EventManager#call(ValueEvent, Object...)
 */

public class EventManager implements Closeable {
    protected ExecutorService executor = Executors.newCachedThreadPool();
    protected boolean throwOnFail = false;

    protected final List<Listener> listeners = new ArrayList<>();
    protected final Map<Event, Map<Listener, List<Method>>> cache = new HashMap<>();

    protected final Map<Event, ScheduledEventData> scheduledEvents = new ConcurrentHashMap<>();
    protected final AsyncTicker ticker = new AsyncTicker();

    public void fire(Event event, Object... args) {
        getApplicableFunctions(event, args).forEach(func -> {
            try {
                func.execute();
            } catch (Exception e) {
                if (throwOnFail)
                    throw new EventFireException(e);
                else
                    fire(new EventErrorEvent(event, e));
            }
        });
    }

    public <T extends EventResult<T>> ValueList<T> call(ValueEvent<T> event, Object... args) {
        var values = new ValueList<T>();

        getApplicableFunctions(event, args).forEach(func -> {
            try {
               var value = (EventResult<T>) func.executeForValue();

               values.add(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return values;
    }

    private List<JavaFunction> getApplicableFunctions(Event event, Object... args) {
        var eventMethods = cache.get(event);

        if (eventMethods == null)
            eventMethods = findMethods(event);

        var arguments = new Object[args.length + 1];
        arguments[0] = event;

        System.arraycopy(args, 0, arguments, 1, args.length);

        var functions = new ArrayList<JavaFunction>();

        for (Entry<Listener, List<Method>> methodEntry : eventMethods.entrySet()) {
            var methods = methodEntry.getValue();

            var applicableMethods = methods.stream()
                    .filter(m -> isApplicable(m, arguments))
                    .filter(m -> m.getReturnType().getTypeName().equals(event.getReturnType()))
                    .map(m -> new JavaFunction(methodEntry.getKey(), m, arguments))
                    .collect(Collectors.toList());

            functions.addAll(applicableMethods);
        }

        return functions;
    }

    public <T extends EventResult<T>> CompletableFuture<ValueList<T>> callAsync(ValueEvent<T> event, Object... args) {
        return CompletableFuture.supplyAsync(() -> call(event, args), executor);
    }

    public CompletableFuture<Void> fireAsync(Event event, Object... args) {
        return CompletableFuture.runAsync(() -> fire(event, args), executor);
    }

    public void scheduleEvent(Event event, Instant time, Object... args) {
        scheduledEvents.put(event, new ScheduledEventData(time, args));
    }

    public void scheduleEvent(Event event, long delay, Object... args) {
        var eventTime = Instant.now().plusMillis(delay);

        scheduleEvent(event, eventTime, args);
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void registerListeners(List<Listener> listeners) {
        this.listeners.addAll(listeners);
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
                .withStep(100L)
                .run(task);
    }

    private Map<Listener, List<Method>> findMethods(Event event) {
        var methods = new HashMap<Listener, List<Method>>();

        for (Listener listener : listeners) {
            var classMethods = Arrays.stream(listener.getClass().getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(EventHandler.class))
                    .filter(m -> {
                        var typeParams = m.getParameterTypes();

                        return typeParams.length != 0 && typeParams[0].getTypeName()
                                .equals(event.typeName());
                    })
                    .peek(m -> m.setAccessible(true))
                    .collect(Collectors.toList());

            methods.put(listener, classMethods);
        }

        cache.put(event, methods);

        return methods;
    }

    private boolean isApplicable(Method method, Object[] args) {
        var typeParams = method.getParameterTypes();

        if (typeParams.length != args.length) return false;

        var typesValid = true;

        for (int i = 0; i < args.length; i++) {
            var typeParam = typeParams[i].getTypeName();
            var argType = args[i].getClass().getTypeName();

            if (!typeParam.equals(argType)) {
                typesValid = false;
                break;
            }
        }

        return typesValid;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void setThrowOnFail(boolean throwOnFail) {
        this.throwOnFail = throwOnFail;
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
