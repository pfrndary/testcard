package card.v1.game.implcards;

import card.v1.Card;
import card.v1.Characteristic;
import card.v1.Game;
import card.v1.Type;
import card.v1.game.event.EventType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ClairDuCompte extends Card {

    public ClairDuCompte(Game game) {
        super(Type.UNIT, 1, 1, 3, game);
    }

    @Override
    public void play() {
        super.play();
        watch(EventType.HEAL);

    }

    @Override
    public void trigger(EventType eventType, Map<Characteristic, Object> data) {
        super.trigger(eventType, data);
        if (eventType.equals(EventType.HEAL)) {
            getOwner().draw();
            final EnumMap<Characteristic, Object> notifyData = getNotifyData(getOwner(), this, Collections.emptyList(), 0);
            getDispatcher().dispatchEvent(EventType.NOTIFY_PLAYER, notifyData);
        }
    }
}