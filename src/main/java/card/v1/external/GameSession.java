package card.v1.external;

import card.v1.*;
import card.v1.game.event.Dispatcher;
import card.v1.game.event.EventType;
import card.v1.game.event.Watcher;
import card.v1.game.implcards.AuspiceFuneste;
import card.v1.game.implcards.ClairDuCompte;
import card.v1.game.implcards.Heal4Ttlm;
import card.v1.game.implcards.TotemKiPioche;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSession extends Watcher {

    private final Game game;

    public GameSession(Dispatcher dispatcher, Map<String, List<Class<? extends Card>>> cardsPerPlayerId) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(dispatcher);
        watch(EventType.NOTIFY_PLAYER);
        game = new Game(dispatcher);
        List<Player> players = new ArrayList<>();
        for (Map.Entry<String, List<Class<? extends Card>>> entry : cardsPerPlayerId.entrySet()) {
            final List<Class<? extends Card>> cardsClass = entry.getValue();
            final List<Card> listOfCards = new ArrayList<>();
            for (Class<? extends Card> aClass : cardsClass) {
                final Card card = aClass.getConstructor(Game.class).newInstance(game);
                listOfCards.add(card);
            }
            final Player p1 = new Player(entry.getKey(), listOfCards, game.getDispatcher());
            p1.initWatch();
            players.add(p1);
        }
        game.setPlayers(players);
        game.init();
        final Player player = game.getPlayerTurn();

        player.draw();
        player.draw();
        player.draw();
        player.draw();
        final List<Card> hand = player.getHand();
        // int rndIdx = Long.valueOf(System.currentTimeMillis() % players.size()).intValue();
        int index = 0;
        for (Card card : hand) {
            if (card.getClass().equals(ClairDuCompte.class)) {
                break;
            }
            index++;
        }
        player.play(index);
        player.endTurn();
    }

    public static void main(String... s) {
        Map<String, List<Class<? extends Card>>> gameData = new HashMap<>();
        List<Class<? extends Card>> list = new ArrayList<>();
        list.add(AuspiceFuneste.class);
        list.add(Heal4Ttlm.class);
        list.add(ClairDuCompte.class);
        list.add(TotemKiPioche.class);
        gameData.put("pf", list);

        list = new ArrayList<>();
        list.add(ClairDuCompte.class);
        list.add(Heal4Ttlm.class);
        list.add(Heal4Ttlm.class);
        list.add(TotemKiPioche.class);
        gameData.put("fc", list);

        try {
            GameSession gs = new GameSession(new Dispatcher(), gameData);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void trigger(EventType eventType, Map<Characteristic, Object> data) {
        if (eventType.equals(EventType.NOTIFY_PLAYER)) {
            final Player p = (Player) data.get(Characteristic.PLAYER);
            final Target source = (Target) data.get(Characteristic.SOURCE);
            final Integer strength = (Integer) data.get(Characteristic.STRENGTH);
            final EventType action = (EventType) data.get(Characteristic.EVENT_TYPE);
            final List<Target> target = (List<Target>) data.get(Characteristic.TARGET);
            final PushMessage pushMessage = new PushMessage(p, source, action, strength, target);
            push(p.getId(), pushMessage);
            for (Player player : game.getPlayers()) {
                if (!p.getId().equals(player.getId())) {
                    push(player.getId(), pushMessage);
                }
            }
        }
    }

    public void push(String playerUid, PushMessage pushMessage) {
        System.out.println("@" + playerUid + " > " + pushMessage);
    }

}
