package card.v1.game.implcards;

import card.v1.Card;
import card.v1.Characteristic;
import card.v1.Game;
import card.v1.Type;
import card.v1.game.event.EventType;

import java.util.Map;

public class TotemKiPioche extends Card {

    public TotemKiPioche(Game game) {
        super(Type.UNIT, 0, 3, 3, game, new EventType[0]);
    }

    @Override
    public void watch(EventType... eventTypes) {
        super.watch(eventTypes);
        getGame().getDispatcher().subscribe(EventType.END_TURN, this);
    }

    @Override
    public void trigger(EventType eventType, Map<Characteristic, Object> data) {
        super.trigger(eventType, data);
        if (eventType.equals(EventType.END_TURN) && getOwner().equals(getGame().getPlayerTurn())) {
            getOwner().draw();
        }
    }
}
