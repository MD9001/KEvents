# KEvents
Simple yet powerful Java Events library to implement Event-Driven logic in your project.

# Creating first event
To make event class, simple annotate your class as Event

```java
@Event
public class YourEvent {
     private final String message;
     
     public YourEvent(String message) {
         this.message = message;
     }
     
     public String getMessage() {
         return message;
     }
}
```

Now let's create listener, where we will handle our event:
Sure, you can handle more than 1 event in one listener.

To make Listener class simply annotate it as listener.

```java
@Listener
public class YourListener {
     @EventHandler
     public void onYourEvent(YourEvent e) {
         System.out.println(e.getMessage());
     }
}
```
# Firing created event
After you have created your event, let's create EventManager instance;

```java
public static void main(String[] args) {
    EventManager eventManager = new EventManager();
    eventManager.registerListener(new YourListener()); //Registers your listener class
    
    YourEvent event = new YourEvent("Message");
    
    eventManager.call(event); // prints "Message", as specified in Listener.
    
    eventManager.close();
}
```
If you have more arguments in the function, simply add them to call method of event manager.
```java
@EventHandler
public void onYourEvent(YourEvent e, int yourArg1, String yourArg2) {
    //do smth
}
```
```java
eventManager.call(new YourEvent(), 5, "Yet another arg")
```
# Typed Events
To create an event, calling which you should get a value, specify return value class in @Event annotation.
Note that if the return type you define has a primitive alternative, the primitive
return type in the function should be used (like if you specify Integer.class, create method with <b>int</b> return type)
The same works with Void type (if you specify Void.class explicitly, use <b>void</b> return type)
```java
@Event(typeNames = String.class)
public class SomeEvent {
    
}
```
Great example:
```java
@Event(typeNames = String.class)
class SomeEvent {}

@Listener
class SampleListener {

    @EventHandler(priority = EventPriority.HIGH)
    public String onSomeEvent(SomeEvent e) {
        return "Hello";
    }

    @EventHandler
    public String onAnotherSample(SomeEvent e) {
        return "World";
    }
    
    public class SampleEventCall {
        public static void main(String[] args) {
            try (var manager = new EventManager()) {
                manager.registerListener(new SampleListener());

                CallResult result = manager.call(new SomeEvent());
                List<String> values = result.getValues(String.class); // Get value list with type of String
                
                String message = String.join(" ", result.getValues(String.class));

                System.out.println(message); //Hello world
            }
        }
    }
}
```
Here we specified 2 event handlers with return values.
To specify the right order of execution, simply pass EventPriority
to annotation arguments.

In case you have only one typed event handler you can simply call CallResult#first(Class) method;
```java
CallResult result = eventManager.call(new SampleEvent());
String stringValue = result.first(String.class);
```
# Multi-Typed events
You can specify more than 1 return type to event.
To specify multiple event types pass an array of type classes to your event annotation.
```java
@Event(typeNames = {String.class, Integer.class})
public class YourEvent {}
```
Then to get values of specific type simply call getValues method for list of values or first method for the first result.
For instance, to get integers:
```java
CallResult#getValues(Integer.class) //All Integer values
CallResult#first(String.class) //First String value
```
# Scheduling events
Events can be scheduled by calling schedule method of EventManager object.
```java
CompletableFuture<CallResult> result = eventManager.schedule(new YourEvent(), 1000L); //1000L =  delay 1000 ms before firing event
```

# Cancelling events
Simply implement Cancellable interface to your event and furthermore call setCancelled(true/false)
method;

```java
@Event
public class YourEvent implements Cancellable {
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = true;
    }
}
```
```java
@Listener
public class YourListener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onYourEvent(YourEvent e) {
        System.out.println("Cancelling event");
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onSameYourEvent(YourEvent e) {
        System.out.println("Will not be printed");
    }
}
```
If you need specific method to be executed even after the event was cancelled,
specify ignoreCancelled argument in EventHandler annotation.
```java
    @EventHandler(ignoreCancelled = true)
    public void onSameYourEvent(YourEvent e) {
        System.out.println("Now will be printed");
    }
```
# TODO:
1. Add project to maven repository.
2. Create detailed examples.
