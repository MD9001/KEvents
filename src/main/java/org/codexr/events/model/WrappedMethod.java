package org.codexr.events.model;

import org.codexr.events.annotations.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Wrapper class for Method for not recalculating unneeded things every time.
 */
public class WrappedMethod {
    protected final Method method;

    protected final String[] typeParams;
    protected final String returnType;

    protected final EventHandler annotation;

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

    public Method getMethod() {
        return method;
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
