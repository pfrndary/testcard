package card.v2.event;

import card.v2.Game;
import card.v2.ingame.CardInGame;
import card.v2.ingame.PlayerInGame;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class EventInfo {

    // Watcher ==== Actor
    private final Game game;
    private Watcher who;
    private CardInGame that;
    private Watcher[] to;


    public EventInfo(Game game, Watcher who, CardInGame that, Watcher[] to) {
        this.game = game;
        this.who = who;
        this.that = that;
        this.to = to;
    }


    public Watcher getWho() {
        return who;
    }

    public CardInGame getThat() {
        return that;
    }

    public Watcher[] getTo() {
        return to;
    }

    public Game getGame() {
        return game;
    }

    public static EventInfo fromJson(Game game, JsonObject body) {
        final PlayerInGame who = game.getPlayerByUid(body.getString("who"));
        final CardInGame that = who.getCardByUidNullable(body.getString("that"));
        final JsonArray targetsJsonArray = body.getJsonArray("targets");
        final List<Watcher> targets = new ArrayList<>();
        searchTargets:
        for (Object o : targetsJsonArray.getList()) {
            PlayerInGame playerByUid = game.getPlayerByUid(o.toString());
            if (playerByUid != null) {
                targets.add(playerByUid);
            } else {
                for (List<CardInGame> cardInGames : game.getBoard().getBoards().values()) {
                    for (CardInGame cardInGame : cardInGames) {
                        if (cardInGame.getUuid().equals(o.toString())) {
                            targets.add(cardInGame);
                            continue searchTargets;
                        }
                    }
                }
                // Nothing found => bad request
            }
        }
        return new EventInfo(game, who, that, targets.toArray(new Watcher[targets.size()]));
    }

    public String asJson() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("\"");
        stringBuilder.append("game");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append("\"");
        stringBuilder.append("futurGameId");
        stringBuilder.append("\"");
        stringBuilder.append(",");
// TODO adapter au JS généré
        stringBuilder.append("\"");
        stringBuilder.append("who");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append("\"");
        stringBuilder.append(who);
        stringBuilder.append("\"");
        stringBuilder.append(",");

        stringBuilder.append("\"");
        stringBuilder.append("");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append("\"");
        stringBuilder.append(that);
        stringBuilder.append("\"");
        stringBuilder.append(",");

        stringBuilder.append("\"");
        stringBuilder.append("to");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append("\"");
        stringBuilder.append(to);
        stringBuilder.append("\"");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
