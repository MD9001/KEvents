package kvant.events.handler;

import kvant.events.handler.priority.EventPriority;
import kvant.events.model.EventResult;

public interface Handler extends Comparable<Handler> {
    void execute() throws Exception;

    //Shouldn't throw exception, instead pass exception as event result argument
    EventResult<?> executeForValue();

    EventPriority priority();

    boolean ignoreCancelled();
}
