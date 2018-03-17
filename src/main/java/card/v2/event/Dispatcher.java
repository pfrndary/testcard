package card.v2.event;

import java.util.*;

public class Dispatcher {

    private final Map<Trigger, Set<Watcher>> watcherEnumMap = new EnumMap<>(Trigger.class);


    public void subscribe(Trigger trigger, Watcher watcher) {
        watcherEnumMap.computeIfAbsent(trigger, e -> new HashSet<>()).add(watcher);
    }

    public void unsubscribe(Watcher watcher) {
        for (Map.Entry<Trigger, Set<Watcher>> entry : watcherEnumMap.entrySet()) {
            entry.getValue().remove(watcher);
        }
    }

    public void dispatchEvent(Trigger eventType, EventInfo data) {
        for (Watcher watcher : watcherEnumMap.getOrDefault(eventType, Collections.emptySet())) {
            watcher.trigger(eventType, data);
        }
    }
}
