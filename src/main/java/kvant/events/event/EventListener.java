package kvant.events.event;

import kvant.events.annotations.EventHandler;
import kvant.events.annotations.Listener;
import kvant.events.handler.Handler;
import kvant.events.handler.MethodHandler;
import kvant.events.model.WrappedMethod;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class EventListener {
    private final Object listener;
    private final String name;

    private final Map<String, WrappedMethod[]> eventHandlers = new HashMap<>();

    public EventListener(Object listener) {
        this.listener = listener;
        this.name = listener.getClass().getTypeName();

        var clazz = listener.getClass();

        if (clazz.getAnnotation(Listener.class) == null) {
            throw new IllegalArgumentException("Not Listener class");
        }

        var methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(this::isEventHandler)
                .peek(m -> m.setAccessible(true))
                .map(WrappedMethod::new)
                .collect(Collectors.toList());

        for (WrappedMethod method : methods) {
            var args = method.getTypeParams();

            if (args.length < 1) continue;

            var eventType = args[0];

            if (eventType == null) continue;
            var eventMethods = eventHandlers.get(eventType);

            if (eventMethods == null) {
                eventMethods = new WrappedMethod[0];
            }

            var list = new ArrayList<>(Arrays.asList(eventMethods));
            list.add(method);

            eventHandlers.put(eventType, list.toArray(WrappedMethod[]::new));
        }
    }

    private boolean isEventHandler(Method method) {
        return method.getAnnotation(EventHandler.class) != null;
    }

    private boolean isApplicable(WrappedMethod method, EventObject event, Object[] args) {
        var typeParams = method.getTypeParams();

        if (typeParams.length != args.length) return false;

       for (int i = 0; i < args.length; i++) {
            var typeParam = typeParams[i];
            var argType = args[i].getClass().getTypeName();

            if (!typeParam.equals(argType)) {
                return false;
            }
        }

        var returnType = method.getReturnType();

        return event.getTypeNames().contains(returnType);
    }

    private Handler createMethodHandler(Object listener, WrappedMethod method, Object[] args) {
        var annotation = method.getAnnotation();

        return new MethodHandler(annotation.priority(), annotation.ignoreCancelled(),
                listener, method, args);
    }

    public List<Handler> getMethodHandlers(EventObject event, Object[] args) {
        var methods = eventHandlers.get(event.getEventName());

        if (methods == null) return new ArrayList<>();

        var handlers = new ArrayList<Handler>();

        for (int i = 0; i < methods.length; i++) {
            var method = methods[i];

            if (!isApplicable(method, event, args)) continue;

            handlers.add(createMethodHandler(listener, method, args));
        }

        return handlers;
    }

    public String getName() {
        return name;
    }
}
