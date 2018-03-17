package card.v1.external;

import card.v1.Player;
import card.v1.Target;
import card.v1.game.event.EventType;

import java.util.List;

public final class PushMessage {
    private final Player to;
    private final Target source;
    private final EventType eventType;
    private final Integer strength;
    private final List<Target> target;

    public PushMessage(Player to, Target source, EventType eventType, Integer strength, List<Target> target) {
        this.to = to;
        this.source = source;
        this.eventType = eventType;
        this.strength = strength;
        this.target = target;
    }

    public Target getSource() {
        return source;
    }

    public EventType getEventType() {
        return eventType;
    }

    public List<Target> getTarget() {
        return target;
    }

    public Player getTo() {
        return to;
    }

    public Integer getStrength() {
        return strength;
    }

    @Override
    public String toString() {
        return "PushMessage{" +
                "to=" + to +
                ", source=" + source +
                ", eventType=" + eventType +
                ", strength=" + strength +
                ", target=" + target +
                '}';
    }
}
