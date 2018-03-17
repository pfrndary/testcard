package card.v1.game.implcards;

import card.v1.*;
import card.v1.game.event.EventType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Heal4Ttlm extends Card {

    public Heal4Ttlm(Game game) {
        super(Type.SPELL, 0, 0, 0, game);
    }

    @Override
    public void play() {
        super.play();
        final Player owner = getOwner();
        final Target source = this;
        final Map<Player, List<Card>> boards = getGame().getBoard().getBoards();
        final List<Target> cardsOnBoard = boards.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        final EnumMap<Characteristic, Object> notifyData = getNotifyData(owner, source, cardsOnBoard, 4);
        getGame().getDispatcher().dispatchEvent(EventType.HEAL_ALL, notifyData);
    }


}
