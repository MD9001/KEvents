package kvant.events.handler;

import kvant.events.event.EventPriority;

public interface Handler extends Comparable<Handler> {
    Object execute() throws Exception;

    EventPriority priority();

    boolean ignoreCancelled();
}