package kvant.events.model;

import java.time.Instant;

/**
 * Represents the scheduled event's information
 */

public final class ScheduledEventData {
    private final Instant fireTime;
    private final Object[] args;

    public ScheduledEventData(Instant fireTime, Object[] args) {
        this.fireTime = fireTime;
        this.args = args;
    }

    public Instant getFireTime() {
        return fireTime;
    }

    public Object[] getArgs() {
        return args;
    }
}
