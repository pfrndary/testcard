package card.v2.event;

import java.util.UUID;

public abstract class Watcher {

    private final String uuid = UUID.randomUUID().toString();
    private final Dispatcher dispatcher;

    public Watcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void watch(Trigger... triggers) {
        for (Trigger trigger : triggers) {
            dispatcher.subscribe(trigger, this);
        }
    }

    public abstract void trigger(Trigger trigger, EventInfo data);

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public String getUuid() {
        return uuid;
    }
}
