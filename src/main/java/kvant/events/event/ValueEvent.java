package kvant.events.event;

/**
 * Event, event handler of which contains return statement.
 * @param <T> - Type of object should be returned
 */

public abstract class ValueEvent<T> implements Event {
    private final Class<T> clazz;
    private final String typeName;

    protected ValueEvent(Class<T> clazz) {
        this.clazz = clazz;
        this.typeName = clazz.getTypeName();
    }

    @Override
    public String getReturnType() {
        return typeName;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
