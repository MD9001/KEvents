package kvant.events.handler;

import kvant.events.event.EventPriority;
import kvant.events.model.WrappedMethod;

import java.lang.reflect.InvocationTargetException;

/**
 * Wrapper object for basic Java Methods for further invoking.
 */

public class MethodHandler implements Handler {
    private final EventPriority priority;
    private final boolean ignoreCancelled;

    private final Object target;
    private final WrappedMethod method;
    private final Object[] args;

    public MethodHandler(EventPriority priority, boolean ignoreCancelled, Object target, WrappedMethod method, Object[] args) {
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
        this.target = target;
        this.method = method;
        this.args = args;
    }

    @Override
    public Object execute() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, args);
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
