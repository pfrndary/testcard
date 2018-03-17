package card.web;

import card.v2.Game;
import card.v2.HsTest;
import card.v2.event.Action;
import card.v2.event.Dispatcher;
import card.v2.event.EventInfo;
import card.v2.event.Watcher;
import card.v2.type.Card;
import card.v2.type.Player;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.*;

public class Launcher extends AbstractVerticle {

    private Map<String, Card> allCards;
    private Map<String, String> playersPreparation = new HashMap<>();
    private Map<String, Collection<String>> gamesAndPlayers = new HashMap<>();
    private Map<String, ServerWebSocket> playersWs = new HashMap<>();
    private Map<String, Game> games = new HashMap<>();

    private List<String> waitingPlayers = new ArrayList<>();

    public static void main(String... s) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(Launcher.class.getName());
    }

    private static void validateConfig() {

    }

    @Override
    public void start(Future<Void> fut) throws IOException {

        allCards = HsTest.loadCards();
        StringBuilder jsonAllCards = new StringBuilder();
        for (Card card : allCards.values()) {
            jsonAllCards.append(card.toJson());
            jsonAllCards.append(",");
        }

        final String strJsonAllCards = "[" + jsonAllCards.substring(0, jsonAllCards.length() - 1) + "]";
        vertx
                .createHttpServer()
                .requestHandler(request -> {
                    HttpServerResponse res = request.response();
                    ///////////////////////////////////////////////////////////
                    //      STATIC FILES
                    if (request.path().startsWith("/html")) {
                        res.sendFile("." + request.path());
                        ///////////////////////////////////////////////////////
                        // LIST CARDS
                    } else if (request.path().equals("/cards")) {
                        request.response().end(strJsonAllCards);
                        ///////////////////////////////////////////////////////
                        // MAIN PAGE (TEST)
                    } else if (request.path().equals("/hs")) {
                        res.sendFile("index.html");
                        ///////////////////////////////////////////////////////
                        // NOTHING
                    } else if (request.path().equals("/")) {
                        request.response().end("<h1>Hello from my first Vert.x 3 application</h1>");
                        ///////////////////////////////////////////////////////
                        // PLAY A CARD (GAME ALREADY LAUNCHED)
                    } else if (request.path().startsWith("/play")) {
                        request.bodyHandler(bodyHandler -> {
                            final JsonObject body = bodyHandler.toJsonObject();
                            final Game game = games.get(body.getString("gameSessionId"));
                            final EventInfo eventInfo = EventInfo.fromJson(game, body);
                            Action.PLAY_CARD.consume(eventInfo);
                        });
                        ///////////////////////////////////////////////////////
                        // WS ENDPOINT CALL IT TO BEFORE HOPING ENTERING IN A GAME
                    } else if (request.path().startsWith("/ws")) {
                        String keyPlayerId = request.path().substring(3);
                        // TODO matchmaking
                        if (waitingPlayers.contains(keyPlayerId)) {
                            ServerWebSocket websocket = request.upgrade();
                            websocket.accept();
                            playersWs.put(keyPlayerId, websocket);
                            if (playersWs.size() > 1) {
                                int i = 0;
                                final List<Player> players = new ArrayList<>();
                                final List<ServerWebSocket> websockets = new ArrayList<>();
                                for (Map.Entry<String, ServerWebSocket> entry : playersWs.entrySet()) {
                                    final Player player = new Player(entry.getKey(), Arrays.asList( //
                                            allCards.get("northshire-cleric"), //
                                            allCards.get("northshire-cleric"), //
                                            allCards.get("northshire-cleric"), //
                                            allCards.get("northshire-cleric"), //
                                            allCards.get("northshire-cleric"), //
                                            allCards.get("northshire-cleric"), //
                                            allCards.get("northshire-cleric") //
                                    ));
                                    players.add(player);
                                    websockets.add(entry.getValue());
                                    i++;
                                    if (i == 2) {
                                        break;
                                    }
                                }
                                final Game game = new Game(new Dispatcher(), players, websockets, allCards);
                                game.startGame();
                                games.put(game.getUuid(), game);
                                Action.INITIATE_NEW_TURN.consume(new EventInfo(game, game.getPlayerTurn(), null, new Watcher[0]));
                            }
                            request.response().end();
                        } else {
                            request.response().setStatusCode(400).end();
                        }
                        ///////////////////////////////////////////////////////
                        // NOTIFY THE SERVER YOU WANT TO PLAY A GAME
                    } else if (request.path().equals("/searchGame")) {
                        final String playerid = request.getParam("playerid");
                        final HttpServerResponse response = request.response();
/*                        if (waitingPlayers.size() >= 1) {
                            final String gameSessionId = UUID.randomUUID().toString();
                            final String player1 = waitingPlayers.remove(0);
                            final String player2 = playerid;
                            final ServerWebSocket serverWebSocket1 = playersWs.get(player1);
                            final ServerWebSocket serverWebSocket2 = playersWs.get(player2);
                            serverWebSocket1.writeFinalTextFrame("launch game " + gameSessionId);
                            serverWebSocket2.writeFinalTextFrame("launch game " + gameSessionId);
                            // playersPreparation.put(gameSessionId, players);
                            response.write(gameSessionId);
                            // reponse
                            // ok connecte toi au WS suivant
                            //
                        } else {*/
                        waitingPlayers.add(playerid);
                        final String urlWs = "ws://127.0.0.1:8080/ws";
                        final String responseBody = urlWs + playerid;
                        response.headers().add("Content-Length", Integer.toString(playerid.length() + urlWs.length()));
                        response.write(responseBody);

                        response.end();
                        ///////////////////////////////////////////////////////
                        // THE CALL IS NOT VALID
                    } else {
                        // Reject
                        request.response().setStatusCode(400).end();
                    }
                }).listen(8080, result -> {
            if (result.succeeded()) {
                fut.complete();
            } else {
                fut.fail(result.cause());
            }
        });
        /*final SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);
        final SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
        final BridgeOptions bo = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("/client.register"))
                .addOutboundPermitted(new PermittedOptions().setAddress("service.ui-message"));
        sockJSHandler.bridge(bo, event -> {
            System.out.println("A websocket event occurred: " + event.type() + "; " + event.getRawMessage());
            event.complete(true);
        });

        final Router router = new RouterImpl(vertx);
        router.route("/client.register" + "/*").handler(sockJSHandler);*/
    }
}
