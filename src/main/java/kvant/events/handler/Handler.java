package kvant.events.handler;

import kvant.events.model.EventResult;

public interface Handler {
    void execute() throws Exception;

    EventResult<?> executeForValue() throws Exception;
}
