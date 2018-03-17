package card.v1;

import card.v1.game.event.Dispatcher;
import card.v1.game.event.EventType;
import card.v1.game.event.Watcher;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Game extends Watcher {

    private List<Player> players;
    private final Board board = new Board();
    private Player playerTurn;
    private int indexTurn = 0;

    public Game(Dispatcher dispatcher) {
        super(dispatcher);
    }

    public void init() {
        getDispatcher().subscribe(EventType.HEAL_ALL, this);
        getDispatcher().subscribe(EventType.KILL_ALL, this);
        getDispatcher().subscribe(EventType.END_TURN, this);
        indexTurn = Long.valueOf(System.currentTimeMillis() % players.size()).intValue();
        playerTurn = players.get(indexTurn);
    }

    @Override
    public void trigger(EventType eventType, Map<Characteristic, Object> data) {
        if (EventType.HEAL_ALL.equals(eventType)) {
            for (List<Card> cards : board.getBoards().values()) {
                for (Card card : cards) {
                    Integer strength = (Integer) data.get(Characteristic.STRENGTH);
                    card.heal(strength);
                }
            }
            // Player p = (Player) data.get(Characteristic.PLAYER);
            getDispatcher().dispatchEvent(EventType.NOTIFY_PLAYER, data);
        } else if (EventType.END_TURN.equals(eventType)) {
            final Player playerNextTurn = nextTurn();
            data.put(Characteristic.TARGET, Collections.singletonList(playerNextTurn));
            data.put(Characteristic.EVENT_TYPE, EventType.END_TURN);
            getDispatcher().dispatchEvent(EventType.NOTIFY_PLAYER, data);
        }
    }

    public static EnumMap<Characteristic, Object> getNotifyData(Player dest, Target source, List<Target> target, int strength) {
        final EnumMap<Characteristic, Object> map = new EnumMap<>(Characteristic.class);
        map.put(Characteristic.PLAYER, dest);
        map.put(Characteristic.SOURCE, source);
        map.put(Characteristic.TARGET, target);
        map.put(Characteristic.STRENGTH, strength);
        return map;
    }

    public Player nextTurn() {
        indexTurn++;
        if (indexTurn >= players.size()) {
            indexTurn = 0;
        }
        playerTurn = players.get(indexTurn);
        getDispatcher().dispatchEvent(EventType.NEW_TURN, Collections.singletonMap(Characteristic.PLAYER, playerTurn));
        return playerTurn;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayers(List<Player> players) {
        for (Player player : players) {
            player.setBoard(getBoard());
        }
        this.players = players;
    }
}
