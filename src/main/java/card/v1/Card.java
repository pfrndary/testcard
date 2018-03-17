package card.v1;

import card.v1.game.event.EventType;
import card.v1.game.event.Watcher;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class Card extends Watcher implements Target {

    private final Game game;
    private String name;
    private Player owner;
    private Type type;
    private Map<Characteristic, Object> characteristics = new EnumMap<>(Characteristic.class);
    private List<EventType> attackEffects;



    @Override
    public void take(EventType eventType, Map<Characteristic, Object> data) {

    }

    public Card(Type type, Integer strength, Integer cost, Integer life, Game game, EventType... effects) {
        super(game.getDispatcher());
        this.type = type;
        this.game = game;
        characteristics.put(Characteristic.STRENGTH, strength);
        characteristics.put(Characteristic.COST, cost);
        characteristics.put(Characteristic.LIFE_MAX, life);
        characteristics.put(Characteristic.LIFE_REMAINING, life);
        attackEffects = Arrays.asList(effects);
    }

    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

    public void play() {

    }

    protected EnumMap<Characteristic, Object> getNotifyData(Player dest, Target source, List<Target> target, int strength) {
        final EnumMap<Characteristic, Object> map = new EnumMap<>(Characteristic.class);
        map.put(Characteristic.PLAYER, dest);
        map.put(Characteristic.SOURCE, source);
        map.put(Characteristic.TARGET, target);
        map.put(Characteristic.STRENGTH, strength);
        return map;
    }

    public void heal(Integer strength) {
        final Integer lifeMax = (Integer) characteristics.get(Characteristic.LIFE_MAX);
        final Integer lifeRemaining = (Integer) characteristics.get(Characteristic.LIFE_REMAINING);
        final Integer newLife = Math.min(lifeMax, lifeRemaining + strength);
        characteristics.put(Characteristic.LIFE_REMAINING, newLife);
    }

    @Override
    public void trigger(EventType eventType, Map<Characteristic, Object> data) {
        if (eventType.equals(EventType.KILLED)) {
            getDispatcher().unsubscribe(this);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }


    public Game getGame() {
        return game;
    }

    public Type getType() {
        return type;
    }

    public Map<Characteristic, Object> getCharacteristics() {
        return characteristics;
    }


    public List<EventType> getAttackEffects() {
        return attackEffects;
    }
}
