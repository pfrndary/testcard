package card.v2.ingame;

import card.v2.event.*;
import card.v2.type.Card;
import card.v2.type.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CardInGame extends Watcher {

    private PlayerInGame owner;
    private final Card card;
    private final String uuid = UUID.randomUUID().toString();
    private int currentLife;
    private int nbrAttackRemaining;

    public CardInGame(PlayerInGame owner, Card card, Dispatcher dispatcher) {
        super(dispatcher);
        this.owner = owner;
        this.card = card;
    }

    public void initWatch() {
        this.currentLife = card.getLife();
        for (Effect effect : card.getTriggers()) {
            watch(effect.getTrigger());
        }
    }

    public void initNbrAttack() {
        nbrAttackRemaining = card.getNbrAttackPerTurn();
    }

    @Override
    public void trigger(Trigger trigger, EventInfo data) {
        // TODO une map dans Card ? <Trigger, Consequence>
        final List<Effect> triggers = card.getTriggers();
        final List<Effect> collect = triggers.stream().filter(effect -> effect.getTrigger().equals(trigger)).collect(Collectors.toList());
        for (Effect effect : collect) {
            if (effect.getAction().equals(Action.DRAW)) {
                Action.DRAW.consume(new EventInfo(data.getGame(), owner, null, null));
                /*final PlayerInGame to = (PlayerInGame) data.getWho();
                final Player playerData = to.getPlayerData();
                if (owner.getPlayerData().getName().equals(playerData.getName())) {
                    to.draw();
                }*/
            }
        }
    }

    public void attack(Watcher target) {
        // ensure is UNIT
        if (target instanceof CardInGame) {
            CardInGame card = (CardInGame) target;
            card.setCurrentLife(card.getCurrentLife() - this.getCard().getAttack());
            this.setCurrentLife(this.getCurrentLife() - card.getCard().getAttack());
            if (this.isDead()) {
                // TODO pour les 2 cartes publier si dead un event
                // getDispatcher().subscribe();
            }
        } else {
            // TODO attack player
        }
    }

    public boolean heal(Watcher sourceOfHeal, Integer strength) {
        int lifeBeforeHeal = currentLife;
        currentLife = Math.min(card.getLife(), currentLife + strength);
        return lifeBeforeHeal != currentLife;
    }

    public Card getCard() {
        return card;
    }

    public int getCurrentLife() {
        return currentLife;
    }


    public void setCurrentLife(int currentLife) {
        this.currentLife = currentLife;
    }

    @Override
    public String toString() {
        return "CardInGame{" +
                "card=" + card +
                ", currentLife=" + currentLife +
                '}';
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isDead() {
        return currentLife <= 0;
    }
}
