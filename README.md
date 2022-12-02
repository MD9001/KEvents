# KEvents
Simple yet powerful library to implement Event-Driven logic in your project.

# Creating first event
By default, events are separated in 2 types: VoidEvent and ValueEvent.

The difference between this events, is that the second one (ValueEvent) can
return value after call.

Let's create the first VoidEvent, containig any your message in constructor.

```java
public class YourEvent extends VoidEvent {
     private final String message;
     
     public YourEvent(String message) {
         this.message = message;
     }
     
     public String getMessage() {
         return message;
     }
     
     @Override
     public String name() {
         return "YourEventName" //By default name() returns simple class name of your class.
     }
}
```

Now let's create listener, where we will handle our event:
Sure, you can handle more than 1 event in one listener.

```java
public class YourListener implements Listener {
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
    eventManager.setThrowOnFail(false); //Set's if event manager should throw exception on event fire error.
    eventManager.registerListener(new YourListener()); //Registers your listener class
    
    YourEvent event = new YourEvent("Message");
    
    eventManager.fire(event); // prints "Message", as specified in Listener.
    
    eventManager.close();
}
```
# Value Events
To create an event, calling which you should get a value we should extend ValueEvent class.
```java
public class YourValueEvent extends ValueEvent<String> {
    public YourValueEvent() {
        super(String.class); // return value of your event handler method
    }
}
```
Then mention in listener:
```java
@EventHandler
public String onYourValueEvent(YourValueEvent e) {
    return "Hello world"
}
```
To call value event you can use
```java
ValueList<String> values = manager.call(new YourValueEvent()); // returns ValueList, cause you may have more than 1 handler method for your event.
```
As we have only one handler method, you can get first return value of your method by calling
```java
String s = values.single() //EventResult<String>. Here you can check for exception by calling .hasException();
    .getValue(); //String
    
//s == "Hello world", as specified in return of onYourValueEvent method;
```
# Scheduling events
Events can be scheduled by calling schedule method of EventManager object.
```java
eventManager.schedule(new YourEvent(), 1000L); //1000L =  delay 1000 ms before firing event
eventManager.schedule(new YourEvent(), Instant.now().plusMillis(1000L)); // Or specify concrete time.
```
If you schedule a ValueEvent, then you can get it's value by handling DelayedValueEvent in your listener
```java
@EventHandler
public void onValueReceived(DelayedValueEvent e) {
    if (e.getEvent().name().equals("YourValueEvent") {
        ValueList<String> values = e.forClass(String.class); // Get value list of String type.
        
        String value = values.single().getValue(); //gets first value from function. If you have multiple handler function for 1 event, use getValues();
        System.out.println(value); //prints "Hello world" as returned in onYourValueEvent;
    }
}
```
To enable event scheduling, you should call handleTicker() method of EventManager.
```java
eventManager.handleTicker();
```

Some basic answers to basic questions:

1. Yes, you can create multiple listeners classes and register them.
2. You can pass more arguments to @EventHandler functions.
3. No, (at least now) you can't have several return types for 1 ValueEvent.


```
TODO:
Add project to maven repository.
Refactor ValueEvent scheduling in EventManager.
```
