package card.v2.js;

import card.v2.event.Action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Toolbox {

    public static final String WS_JS_FILE_NAME = "ws-events.js";

    private static StringBuilder generateJsFunctions() {
        final Action[] actions = Action.values();
        final StringBuilder functions = new StringBuilder();
        final StringBuilder saveFunctionsInMap = new StringBuilder();
        saveFunctionsInMap.append("let actions = {};\n");
        for (Action action : actions) {
            functions.append("function on").append(action.name()).append("(data) {\n");
            functions.append("\tconsole.log('who = '+data.who);\n");
            functions.append("\tconsole.log('did = ").append(action.name()).append("');\n");
            functions.append("\tconsole.log('to = '+data.to);\n");
            functions.append("\tconsole.log('that = '+data.that);\n");
            functions.append("}\n");
            saveFunctionsInMap.append("actions.").append(action.name()).append("=on").append(action.name()).append(";\n");
        }
        functions.append(saveFunctionsInMap);
        return functions;

    }

    private static StringBuilder generateWsPart() {
        final StringBuilder wsJs = new StringBuilder();
        wsJs.append("function connectWsToUrl(url) {\n");
        wsJs.append("\tvar exampleSocket = new WebSocket(url);\n");
        wsJs.append("\texampleSocket.onmessage = function (event) {\n");
        wsJs.append("\t    if (event.type && event.data) {\n");
        wsJs.append("\t       actions[event.type](event.data);\n");
        wsJs.append("\t    } else {\n");
        wsJs.append("\t         console.log('bad websocket data : '+event);\n");
        wsJs.append("\t    }\n");
        wsJs.append("\t}\n");
        wsJs.append("}\n");
        return wsJs;
    }

    private static StringBuilder generateSearchGamePart() {
        final StringBuilder sgJs = new StringBuilder();
        sgJs.append("function searchGame(playerId) {\n");
        sgJs.append("   $.get('/searchGame?playerid='+playerId, function(data) {\n");
        sgJs.append("     connectWsToUrl(data);\n");
        sgJs.append("     console.log(data);\n");
        sgJs.append("   });\n");
        sgJs.append("}\n");
        return sgJs;
    }

    public static void generateWholeJs() {
        final String s = generateJsFunctions().append(generateWsPart()).append(generateSearchGamePart()).toString();
        try {
            final Path file = Files.createFile(Paths.get("./" + WS_JS_FILE_NAME));
            Files.write(file, s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String... s) {
        generateWholeJs();
    }

}
