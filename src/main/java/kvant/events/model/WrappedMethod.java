package kvant.events.model;

import kvant.events.annotations.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Wrapper class for Method for not recalculating unneeded things every time.
 */
public class WrappedMethod {
    private final Method method;

    private final String[] typeParams;
    private final String returnType;

    private final EventHandler annotation;

    public WrappedMethod(Method method) {
        this.method = method;

        typeParams = Arrays.stream(method.getParameterTypes()).map(Class::getTypeName)
                .toArray(String[]::new);
        returnType = method.getReturnType().getTypeName();

        annotation = method.getAnnotation(EventHandler.class);
    }

    public Object invoke(Object target, Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, args);
    }

    public String[] getTypeParams() {
        return typeParams;
    }

    public String getReturnType() {
        return returnType;
    }

    public EventHandler getAnnotation() {
        return annotation;
    }
}
