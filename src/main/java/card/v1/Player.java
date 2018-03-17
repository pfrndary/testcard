package card.v1;

import card.v1.game.event.Dispatcher;
import card.v1.game.event.EventType;
import card.v1.game.event.Watcher;

import java.util.*;

public class Player extends Watcher implements Target {

    private String uuid = UUID.randomUUID().toString();
    private final String name;
    private int maxActionPoint;
    private int currentActionPoint;
    private int penaltyActionPoint;
    private Board board;
    private final LinkedList<Card> deck;
    private final List<Card> initialdeck;
    private final List<Card> hand = new ArrayList<>();
    private final List<Card> cimetery = new ArrayList<>();


    public Player(String name, List<Card> cards, Dispatcher dispatcher) {
        super(dispatcher);
        this.name = name;
        this.initialdeck = new ArrayList<>();
        this.initialdeck.addAll(cards);
        this.deck = new LinkedList<>();
        Collections.shuffle(this.initialdeck);
        this.deck.addAll(this.initialdeck);
        maxActionPoint = 1;
    }

    @Override
    public String getId() {
        return uuid;
    }

    public void initWatch() {
        watch(EventType.NEW_TURN, EventType.HEAL, EventType.ATTACK);
    }

    @Override
    public void take(EventType eventType, Map<Characteristic, Object> data) {

    }

    @Override
    public void trigger(EventType eventType, Map<Characteristic, Object> data) {
        if (eventType.equals(EventType.NEW_TURN)) {
            applyBeginningNewTurn();
            draw();
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                '}';
    }

    public void applyBeginningNewTurn() {
        maxActionPoint++;
        currentActionPoint = maxActionPoint;
    }

    public Card draw() {
        if (deck.isEmpty()) {
            return null;
        }
        final Card card = deck.removeFirst();
        hand.add(card);
        return card;
    }

    public void play(int i, Target... targets) {
        final Card card = hand.remove(i);
        card.play();
        List<EventType> events = card.getAttackEffects();
        for (Target target : targets) {
            for (EventType event : events) {
                target.take(event, card.getCharacteristics());
            }
        }
        final Integer strength = (Integer) card.getCharacteristics().get(Characteristic.STRENGTH);
        final Map<Characteristic, Object> data = Game.getNotifyData(this, card, Arrays.asList(targets), strength);
        data.put(Characteristic.EVENT_TYPE, EventType.PLAY);
        getDispatcher().dispatchEvent(EventType.NOTIFY_PLAYER, data);
    }

    public void destroyCardsOnBoard() {
        final List<Card> playerCards = board.getPlayerCards(this);
        for (Card playerCard : playerCards) {
            playerCard.trigger(EventType.KILLED, Collections.emptyMap());
        }
        cimetery.addAll(playerCards);
        board.getPlayerCards(this).clear();
    }

    public void endTurn() {
        EnumMap<Characteristic, Object> notifyData = Game.getNotifyData(this, this, Collections.emptyList(), 0);
        getDispatcher().dispatchEvent(EventType.END_TURN, notifyData);
    }

    public LinkedList<Card> getDeck() {
        return deck;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getName() {
        return name;
    }

    public int getMaxActionPoint() {
        return maxActionPoint;
    }

    public int getCurrentActionPoint() {
        return currentActionPoint;
    }

    public int getPenaltyActionPoint() {
        return penaltyActionPoint;
    }

    public List<Card> getInitialdeck() {
        return initialdeck;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getCimetery() {
        return cimetery;
    }


}
