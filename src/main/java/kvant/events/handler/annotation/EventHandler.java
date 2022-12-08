package kvant.events.handler.annotation;

import kvant.events.handler.priority.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Event handler annotation.
 * Methods annotated with this are gathered
 * in EventDispatcher to call function for corresponding event.
 *
 * @see EventPriority
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
    EventPriority priority() default EventPriority.NORMAL;

    boolean ignoreCancelled() default false;
}