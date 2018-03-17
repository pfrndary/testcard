package card.v1.game.implcards;

import card.v1.*;
import card.v1.game.event.EventType;

import java.util.Map;

public class AuspiceFuneste extends Card {

    public AuspiceFuneste(Game game) {
        super(Type.UNIT, 0, 2, 7, game);
    }

    @Override
    public void play() {
        super.play();
        watch(EventType.NEW_TURN);
    }

    @Override
    public void trigger(EventType eventType, Map<Characteristic, Object> data) {
        super.trigger(eventType, data);
        if (EventType.NEW_TURN.equals(eventType) && getOwner().equals(getGame().getPlayerTurn())) {
            for (Player player : getGame().getPlayers()) {
                player.destroyCardsOnBoard();
            }
            // board
        }
        // getNotifyData();
        // TODO dispatch event to the opponent
    }
}
