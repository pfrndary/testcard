package card.v2.ingame;

import card.v2.Game;
import card.v2.event.Action;
import card.v2.event.EventInfo;
import card.v2.event.Trigger;
import card.v2.event.Watcher;
import card.v2.type.Card;
import card.v2.type.Player;
import io.vertx.core.http.ServerWebSocket;

import java.util.*;

public class PlayerInGame extends Watcher {

    private final Game game;
    private final Player playerData;
    private ServerWebSocket wsDispatcher;
    private int currentActionPoint;
    private int penaltyActionPoint;
    private int maxActionPoint;
    private int life = 30;

    private final LinkedList<CardInGame> deck;
    private final List<CardInGame> hand = new ArrayList<>();
    private final List<CardInGame> cimetery = new ArrayList<>();

    public PlayerInGame(Game game, Player player) {
        super(game.getDispatcher());
        this.game = game;
        this.playerData = player;
        this.maxActionPoint = 1;
        this.penaltyActionPoint = 0;
        deck = new LinkedList<>();
        for (Card card : player.getInitialdeck()) {
            final CardInGame cardInGame = new CardInGame(this, card, game.getDispatcher());
            deck.add(cardInGame);
        }
        Collections.shuffle(this.deck);
    }

    public PlayerInGame(Game game, Player player, ServerWebSocket wsDispatcher) {
        super(game.getDispatcher());
        this.game = game;
        this.playerData = player;
        this.wsDispatcher = wsDispatcher;
        this.maxActionPoint = 1;
        this.penaltyActionPoint = 0;
        deck = new LinkedList<>();
        for (Card card : player.getInitialdeck()) {
            final CardInGame cardInGame = new CardInGame(this, card, game.getDispatcher());
            deck.add(cardInGame);
        }
        Collections.shuffle(this.deck);
    }

    public void initWatch() {
        watch(Trigger.NEW_TURN, Trigger.DRAW, Trigger.OPPONENT_DRAW, Trigger.HEAL, Trigger.ATTACK, Trigger.NOTIFY_PLAYER);
    }

    public CardInGame draw() {
        if (deck.isEmpty()) {
            return null;
        }
        final CardInGame card = deck.removeFirst();
        hand.add(card);
        return card;
    }

    public CardInGame play(CardInGame card, Watcher... targets) {
        if (!hand.remove(card)) {
            throw new NoSuchElementException(card.toString());
        }
        card.initWatch();
        getGame().getBoard().getPlayerCards(this).add(card);
        return card;
    }

    public CardInGame play(int i, Watcher... targets) {
        final CardInGame playedGame = hand.get(i);
        return play(playedGame, targets);
    }

    public void endTurn() {
        //EnumMap<Characteristic, Object> notifyData = Game.getNotifyData(this, this, Collections.emptyList(), 0);
        getDispatcher().dispatchEvent(Trigger.END_TURN, new EventInfo(Action.END_TURN, game, this, null, new Watcher[]{}));
    }

    @Override
    public void trigger(Trigger eventType, EventInfo data) {
        if (eventType.equals(Trigger.NEW_TURN)) {
            if (data.getWho().getUuid().equals(this.getUuid())) {
                Action.INITIATE_NEW_TURN.consume(data);
            }
        } else if (eventType.equals(Trigger.NOTIFY_PLAYER)) {
            wsDispatcher.writeFinalTextFrame(data.asJson());
        } else if (Trigger.DRAW.equals(eventType)) {
            if (data.getWho().getUuid().equals(this.getUuid())) {
                wsDispatcher.writeFinalTextFrame(data.asJson());
            }
        } else if (Trigger.OPPONENT_DRAW.equals(eventType)) {
            if (!data.getWho().getUuid().equals(this.getUuid())) {
                wsDispatcher.writeFinalTextFrame(data.asJson());
            }
        }
    }

    public void applyBeginningNewTurn() {
        maxActionPoint++;
        currentActionPoint = maxActionPoint;
    }

    public Player getPlayerData() {
        return playerData;
    }

    public Game getGame() {
        return game;
    }

    public LinkedList<CardInGame> getDeck() {
        return deck;
    }

    public List<CardInGame> getHand() {
        return hand;
    }

    public List<CardInGame> getCimetery() {
        return cimetery;
    }

    public int getCurrentActionPoint() {
        return currentActionPoint;
    }

    public int getPenaltyActionPoint() {
        return penaltyActionPoint;
    }

    public int getMaxActionPoint() {
        return maxActionPoint;
    }

    public CardInGame getCardByUidNullable(String carduuid) {
        for (CardInGame cardInGame : getHand()) {
            if (cardInGame.getUuid().equals(carduuid)) {
                return cardInGame;
            }
        }
        return null;
    }
}
