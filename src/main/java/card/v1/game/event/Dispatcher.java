package card.v1.game.event;

import card.v1.Characteristic;

import java.util.*;

public class Dispatcher {

    private final Map<EventType, Set<Watcher>> watcherEnumMap = new EnumMap<>(EventType.class);


    public void subscribe(EventType eventType, Watcher watcher) {
        watcherEnumMap.computeIfAbsent(eventType, e -> new HashSet<>()).add(watcher);
    }

    public void unsubscribe(Watcher watcher) {
        for (Map.Entry<EventType, Set<Watcher>> entry : watcherEnumMap.entrySet()) {
            entry.getValue().remove(watcher);
        }
    }

    public void dispatchEvent(EventType eventType, Map<Characteristic, Object> data) {
        for (Watcher watcher : watcherEnumMap.getOrDefault(eventType, Collections.emptySet())) {
            watcher.trigger(eventType, data);
        }
    }
}
