package card.v2;

import card.v2.event.*;
import card.v2.ingame.CardInGame;
import card.v2.ingame.PlayerInGame;
import card.v2.type.Card;
import card.v2.type.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class HsTest {

    public static void main(String... s) throws IOException {
        final Map<String, Card> allCards = loadCards();
        //System.out.println(allCards);
        final Dispatcher dispatcher = new Dispatcher();
        final Player player1 = new Player("A", Arrays.asList( //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc") //
        ));

        final Player player2 = new Player("B", Arrays.asList( //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc"), //
                allCards.get("clerc") //
        ));
        final Game game = new Game(dispatcher, Arrays.asList(player1, player2), Collections.emptyList(), allCards);

        game.startGame();
        game.nextTurn();
        PlayerInGame playerTurn = game.getPlayerTurn();
        playerTurn.play(0);
        playerTurn.endTurn();

        playerTurn = game.getPlayerTurn();
        String cp2Uuid = playerTurn.play(0).getUuid();
        playerTurn.endTurn();


        playerTurn = game.getPlayerTurn();
        Action.ATTACK.consume(new EventInfo(Action.ATTACK, game, //
                game.getBoard().getBoards().get(playerTurn).get(0),//
                null,//
                new Watcher[]{game.getBoard().getCardById(cp2Uuid)}));
        //System.out.println(game.getBoard().getBoards());

        System.out.println(playerTurn.getHand().size());
            /*Action.HEAL.consume(new EventInfo(game, //
                    game.getBoard().getBoards().get(playerTurn).get(0),//
                    null,//
                    new Watcher[]{game.getBoard().getCardById(cp2Uuid)}));*/
        final Card healSolo = allCards.get("healSolo");
        final CardInGame healingCard = game.summonNewCard(playerTurn, healSolo);
        Action.HEAL.consume(new EventInfo(//
                Action.HEAL, game, //
                playerTurn, //
                healingCard, //
                new Watcher[]{game.getBoard().getPlayerCards(playerTurn).get(0)}
        ));
        // game.getBoard().getPlayerCards(playerTurn).get(0).heal(playerTurn, 1);
        System.out.println(playerTurn.getHand().size());
    }

    public static Map<String, Card> loadCards() throws IOException {
        final File file = new File("test.properties");
        try (InputStream fileInput = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(fileInput);
            int i = 0;
            String cardName;
            Map<String, Card> allCards = new HashMap<>();
            while ((cardName = properties.getProperty("cards." + i + ".name")) != null) {
                final String cardId = properties.getProperty("cards." + i + ".id");
                final String cardPrice = properties.getProperty("cards." + i + ".cost");
                final String relativeUrl = properties.getProperty("cards." + i + ".url");
                final String life = properties.getProperty("cards." + i + ".life");
                final String type = properties.getProperty("cards." + i + ".type");
                final String attack = properties.getProperty("cards." + i + ".attack");
                String strTrigger;
                int j = 0;
                final List<Effect> effects = new ArrayList<>();
                while ((strTrigger = properties.getProperty("cards." + i + ".triggers." + j + ".type")) != null) {
                    final Trigger trigger = Trigger.valueOf(strTrigger);
                    final String strTarget = properties.getProperty("cards." + i + ".triggers." + j + ".target");
                    final TargetType target = TargetType.valueOf(strTarget);
                    final String strAction = properties.getProperty("cards." + i + ".triggers." + j + ".do");
                    final Action action = Action.valueOf(strAction);
                    final Effect effect = new Effect(trigger, target, action);
                    effects.add(effect);
                    j++;
                }
                final CardType cardType = CardType.valueOf(type);
                final Card card = new Card(cardId, cardName, Integer.parseInt(cardPrice), Integer.parseInt(life), Integer.parseInt(attack), relativeUrl, cardType, effects);
                allCards.put(card.getId(), card);
                i++;
            }
            return allCards;
        }
    }

}
