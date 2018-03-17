package card.v2.event;

import card.v2.TargetType;

public class Effect {

    private final Trigger trigger;
    private final TargetType target;
    private final Action action;

    public Effect(Trigger trigger, TargetType target, Action action) {
        this.trigger = trigger;
        this.target = target;
        this.action = action;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public TargetType getTarget() {
        return target;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "Effect{" +
                "trigger=" + trigger +
                ", target=" + target +
                ", action=" + action +
                '}';
    }
}
