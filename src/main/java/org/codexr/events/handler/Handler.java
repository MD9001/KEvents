package org.codexr.events.handler;

import org.codexr.events.event.EventPriority;

public interface Handler extends Comparable<Handler> {
    Object execute() throws Exception;

    EventPriority priority();

    boolean ignoreCancelled();
}