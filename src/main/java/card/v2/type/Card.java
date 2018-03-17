package card.v2.type;

import card.v2.CardType;
import card.v2.event.Effect;

import java.util.List;

public class Card {

    private final String name;
    private final String id;
    private final int price;
    private final int life;
    private final int attack;
    private final int nbrAttackPerTurn = 1;
    private final CardType cardType;
    private final List<Effect> triggers;


    public Card(String id, String name, int price, int life, int attack, CardType cardType, List<Effect> triggers) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.life = life;
        this.attack = attack;
        this.cardType = cardType;
        this.triggers = triggers;
    }


    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getLife() {
        return life;
    }

    public int getAttack() {
        return attack;
    }


    public List<Effect> getTriggers() {
        return triggers;
    }

    public String getId() {
        return id;
    }

    public CardType getCardType() {
        return cardType;
    }

    public int getNbrAttackPerTurn() {
        return nbrAttackPerTurn;
    }

    @Override
    public String toString() {
        return "Card{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", price=" + price +
                ", life=" + life +
                ", attack=" + attack +
                ", cardType=" + cardType +
                ", triggers=" + triggers +
                '}';
    }

    public String toJson() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("\"");
        stringBuilder.append("name");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append("\"");
        stringBuilder.append(name);
        stringBuilder.append("\"");
        stringBuilder.append(",");

        stringBuilder.append("\"");
        stringBuilder.append("id");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append("\"");
        stringBuilder.append(id);
        stringBuilder.append("\"");
        stringBuilder.append(",");

        stringBuilder.append("\"");
        stringBuilder.append("price");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append(price);
        stringBuilder.append(",");

        stringBuilder.append("\"");
        stringBuilder.append("life");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append(life);
        stringBuilder.append(",");

        stringBuilder.append("\"");
        stringBuilder.append("attack");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append(attack);
        stringBuilder.append(",");

        stringBuilder.append("\"");
        stringBuilder.append("nbrAttackPerTurn");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append("1");

        stringBuilder.append(",");

        stringBuilder.append("\"");
        stringBuilder.append("cardType");
        stringBuilder.append("\"");
        stringBuilder.append(":");
        stringBuilder.append("\"");
        stringBuilder.append(cardType.toString());
        stringBuilder.append("\"");
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

}
