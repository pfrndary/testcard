package card.v2;

import card.v2.event.*;
import card.v2.ingame.CardInGame;
import card.v2.ingame.PlayerInGame;
import card.v2.type.Card;
import card.v2.type.Player;
import io.vertx.core.http.ServerWebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Game extends Watcher {

    private Map<String, Card> allCards;
    private List<PlayerInGame> players;
    private final Board board = new Board();
    private PlayerInGame playerTurn;
    private int indexTurn = 0;

    public Game(Dispatcher dispatcher, List<Player> p, List<ServerWebSocket> websockets, Map<String, Card> allCards) {
        super(dispatcher);
        players = new ArrayList<>();
        int i = 0;
        for (Player player : p) {
            final PlayerInGame playerInGame = new PlayerInGame(this, player, websockets.get(i));
            i++;
            playerInGame.initWatch();
            players.add(playerInGame);
        }
        this.allCards = allCards;
    }

    public void startGame() {
        getBoard().setPlayers(players);
        getDispatcher().subscribe(Trigger.HEAL_ALL, this);
        getDispatcher().subscribe(Trigger.KILL_ALL, this);
        getDispatcher().subscribe(Trigger.END_TURN, this);
        indexTurn = Long.valueOf(System.currentTimeMillis() % players.size()).intValue();
        playerTurn = players.get(indexTurn);
    }

    @Override
    public void trigger(Trigger Trigger, EventInfo data) {
        if (Trigger.HEAL_ALL.equals(Trigger)) {
            for (List<CardInGame> cards : board.getBoards().values()) {
                for (CardInGame card : cards) {
                    /*Integer strength = (Integer) data.get(Characteristic.STRENGTH);
                    card.heal(strength);*/
                }
            }
            getDispatcher().dispatchEvent(Trigger.NOTIFY_PLAYER, data);
        } else if (Trigger.END_TURN.equals(Trigger)) {
            nextTurn();
            /*data.put(Characteristic.TARGET, Collections.singletonList(playerNextTurn));
            data.put(Characteristic.EVENT_TYPE, Trigger.END_TURN);*/
            getDispatcher().dispatchEvent(Trigger.NOTIFY_PLAYER, data);
        }
    }

    public CardInGame summonNewCard(PlayerInGame player, Card card) {
        return new CardInGame(player, card, getDispatcher());

    }


    public PlayerInGame nextTurn() {
        indexTurn++;
        if (indexTurn >= players.size()) {
            indexTurn = 0;
        }
        playerTurn = players.get(indexTurn);
        getDispatcher().dispatchEvent(Trigger.NEW_TURN, new EventInfo(Action.INITIATE_NEW_TURN, this, playerTurn, null, new Watcher[]{}));
        return playerTurn;
    }

    public List<PlayerInGame> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerInGame getPlayerTurn() {
        return playerTurn;
    }

    public Map<String, Card> getAllCards() {
        return allCards;
    }

    public PlayerInGame getPlayerByUid(String uid) {
        for (PlayerInGame player : players) {
            if (player.getUuid().equals(uid)) {
                return player;
            }
        }
        throw new NoSuchElementException(uid);
    }

}
