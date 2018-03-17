package card.v1;

import card.v1.game.event.EventType;

import java.util.Map;

public interface Target {

    String getId();

    void take(EventType eventType, Map<Characteristic, Object> data);

}
