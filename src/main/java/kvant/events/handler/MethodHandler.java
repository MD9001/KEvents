package kvant.events.handler;

import kvant.events.model.EventResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper object for basic Java Methods for further invoking.
 */

public class MethodHandler implements Handler {
    private final Object target;
    private final Method method;
    private final Object[] args;

    public MethodHandler(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    @Override
    public void execute() throws InvocationTargetException, IllegalAccessException {
        method.invoke(target, args);
    }

    @Override
    public EventResult<?> executeForValue() throws InvocationTargetException, IllegalAccessException {
        var value = method.invoke(target, args);

        return new EventResult<>(value, null);
    }
}
