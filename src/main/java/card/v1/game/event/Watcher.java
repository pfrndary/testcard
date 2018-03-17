package card.v1.game.event;

import card.v1.Characteristic;

import java.util.Map;

public abstract class Watcher {

    private final Dispatcher dispatcher;

    public Watcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void watch(EventType... eventTypes) {
        for (EventType eventType : eventTypes) {
            dispatcher.subscribe(eventType, this);
        }
    }

    public abstract void trigger(EventType eventType, Map<Characteristic, Object> data);

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}
