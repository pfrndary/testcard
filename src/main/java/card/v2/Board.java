package card.v2;

import card.v2.ingame.CardInGame;
import card.v2.ingame.PlayerInGame;

import java.util.*;

public class Board {

    private final Map<PlayerInGame, List<CardInGame>> boards = new HashMap<>();

    public void setPlayers(Collection<PlayerInGame> players) {
        // proteger contre l'ecase,emt apres lancement de la game
        for (PlayerInGame player : players) {
            boards.put(player, new ArrayList<>());
        }
    }

    public CardInGame getCardById(String uid) {
        for (List<CardInGame> cardInGames : boards.values()) {
            for (CardInGame cardInGame : cardInGames) {
                if (cardInGame.getUuid().equals(uid)) {
                    return cardInGame;
                }
            }
        }
        return null;
    }

    public Map<PlayerInGame, List<CardInGame>> getBoards() {
        return boards;
    }

    public List<CardInGame> getPlayerCards(PlayerInGame player) {
        return boards.get(player);
    }
}
