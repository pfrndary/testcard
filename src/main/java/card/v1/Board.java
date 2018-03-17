package card.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

    private final Map<Player, List<Card>> boards = new HashMap<>();

    public Map<Player, List<Card>> getBoards() {
        return boards;
    }

    public List<Card> getPlayerCards(Player player) {
        return boards.get(player);
    }
}
