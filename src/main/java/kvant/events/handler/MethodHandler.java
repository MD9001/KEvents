package kvant.events.handler;

import kvant.events.handler.priority.EventPriority;
import kvant.events.model.EventResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper object for basic Java Methods for further invoking.
 */

public class MethodHandler implements Handler {
    private final EventPriority priority;
    private final boolean ignoreCancelled;

    private final Object target;
    private final Method method;
    private final Object[] args;

    public MethodHandler(EventPriority priority, boolean ignoreCancelled, Object target, Method method, Object[] args) {
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
        this.target = target;
        this.method = method;
        this.args = args;
    }

    @Override
    public void execute() throws InvocationTargetException, IllegalAccessException {
        method.invoke(target, args);
    }

    @Override
    public EventResult<?> executeForValue() {
        try {
            var value = method.invoke(target, args);

            return new EventResult<>(value, null);
        } catch (Exception e) {
            return new EventResult<>(null, e);
        }
    }

    @Override
    public EventPriority priority() {
        return priority;
    }

    @Override
    public boolean ignoreCancelled() {
        return ignoreCancelled;
    }

    @Override
    public int compareTo(Handler handler) {
        return Integer.compare(priority.getValue(), handler.priority().getValue());
    }
}
