package kvant.events.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper object for basic Java Methods for further invoking.
 */

public class JavaFunction {
    private final Object target;
    private final Method method;
    private final Object[] args;

    public JavaFunction(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public void execute() throws InvocationTargetException, IllegalAccessException {
        method.invoke(target, args);
    }

    public EventResult<?> executeForValue() throws InvocationTargetException, IllegalAccessException {
        var value = method.invoke(target, args);

        return new EventResult<>(value, null);
    }
}
