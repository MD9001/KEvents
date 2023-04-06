package org.codexr.events.handler;

import org.codexr.events.event.EventPriority;
import org.codexr.events.model.WrappedMethod;

import java.lang.reflect.InvocationTargetException;

/**
 * Wrapper object for basic Java Methods for further invoking.
 */

public class MethodHandler implements Handler {
    protected final EventPriority priority;
    protected final boolean ignoreCancelled;

    protected final Object target;
    protected final WrappedMethod wrappedMethod;
    protected final Object[] args;

    public MethodHandler(EventPriority priority, boolean ignoreCancelled, Object target, WrappedMethod method, Object[] args) {
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
        this.target = target;
        this.wrappedMethod = method;
        this.args = args;
    }

    @Override
    public Object execute() throws InvocationTargetException, IllegalAccessException {
        return wrappedMethod.invoke(target, args);
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

    public Object getTarget() {
        return target;
    }

    public WrappedMethod getWrappedMethod() {
        return wrappedMethod;
    }

    public Object[] getArgs() {
        return args;
    }
}
