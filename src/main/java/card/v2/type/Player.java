package card.v2.type;

import java.util.List;
import java.util.UUID;

public class Player {

    private String uuid = UUID.randomUUID().toString();
    private final String name;
    private final List<Card> initialdeck;


    public Player(String name, List<Card> cards) {
        this.name = name;
        this.initialdeck = cards;

    }

    @Override
    public String toString() {
        return "Player{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", initialdeck=" + initialdeck +
                '}';
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public List<Card> getInitialdeck() {
        return initialdeck;
    }


}
