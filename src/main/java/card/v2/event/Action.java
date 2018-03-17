package card.v2.event;

import card.v2.Game;
import card.v2.ingame.CardInGame;
import card.v2.ingame.PlayerInGame;
import card.v2.type.Card;

import java.util.List;
import java.util.function.Consumer;

public enum Action {

    INITIATE_NEW_TURN((eventInfo) -> {
        final Game game = eventInfo.getGame();
        final PlayerInGame playerTurn = game.getPlayerTurn();
        System.out.println(playerTurn.getPlayerData().getName() + " INITIATE_NEW_TURN");
        for (CardInGame cardInGame : game.getBoard().getBoards().get(playerTurn)) {
            cardInGame.initNbrAttack();
        }
        playerTurn.applyBeginningNewTurn();
        Action.valueOf("DRAW").consume(eventInfo);
    }), //
    DRAW((eventInfo) -> {
        PlayerInGame who = (PlayerInGame) eventInfo.getWho();
        System.out.println(eventInfo.getGame().getPlayerByUid(who.getUuid()).getPlayerData().getName() + " DRAW");
        who.draw();
        eventInfo.getGame().getDispatcher().dispatchEvent(Trigger.DRAW, eventInfo);
    }), //
    PLAY_CARD((eventInfo) -> {
        PlayerInGame playerByUid = (PlayerInGame) eventInfo.getWho();// eventInfo.getGame().getPlayerByUid(eventInfo.getWho().getUuid());
        System.out.println(playerByUid.getPlayerData().getName() + " PLAY_CARD");
        playerByUid.play(eventInfo.getThat(), eventInfo.getTo());
    }),//

    ATTACK((eventInfo) -> {
        Watcher who = eventInfo.getWho();
        Watcher[] to = eventInfo.getTo();
        if (who instanceof CardInGame) {
            CardInGame attacker = (CardInGame) who;
            for (Watcher watcher : to) {
                System.out.println(attacker.getCard().getName() + " ATTACK " + watcher.getUuid());
                attacker.attack(watcher);
            }
        } else if (who instanceof PlayerInGame) {
            // if weapon
        }
    }),

    HEAL((eventInfo) -> {
        Watcher[] to = eventInfo.getTo();
        for (Watcher watcher : to) {
            if (watcher instanceof CardInGame) {
                final CardInGame cardInGame = (CardInGame) watcher;
                int strength = eventInfo.getThat().getCard().getAttack();
                boolean healed = cardInGame.heal(eventInfo.getWho(), strength);
                if (healed) {
                    System.out.println(eventInfo.getWho() + " HEALED " + watcher.getUuid());
                    eventInfo.getGame().getDispatcher().dispatchEvent(Trigger.HEAL, eventInfo);
                }
            }
        }
    }),

    KILL_ALL((event) -> {
        // eventInfo.getGame().getBoard().;
    }),

    GEN_NEW_RANDOM_CARD_DEATH_TRIGGER((event) -> {
        // TODO lister toutes les cartes de ce type et en prendre une ua pif
    }),

    SUMMON_2_IMAGE_MIRROIR((eventInfo) -> {
        //
        final PlayerInGame playerTurn = eventInfo.getGame().getPlayerTurn();
        final Card imageMirroir = eventInfo.getGame().getAllCards().get("imageMirroir");
        CardInGame cardInGame1 = eventInfo.getGame().summonNewCard(playerTurn, imageMirroir);
        CardInGame cardInGame2 = eventInfo.getGame().summonNewCard(playerTurn, imageMirroir);
        final List<CardInGame> cardInGames = eventInfo.getGame().getBoard().getBoards().get(playerTurn);
        cardInGames.add(cardInGame1);
        cardInGames.add(cardInGame2);

    }),

    END_TURN((event) -> {

    });

    private Consumer<EventInfo> gameConsumer;

    Action(Consumer<EventInfo> gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    public void consume(EventInfo eventInfo) {
        this.gameConsumer.accept(eventInfo);
    }

}
