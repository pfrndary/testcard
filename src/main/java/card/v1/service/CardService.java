package card.v1.service;

import card.v1.entity.CardLightInfo;

import java.util.Collection;
import java.util.Collections;

public final class CardService {

    private Class<?>[] classes = null;

    public CardService() {
        try {
            classes = Class.forName("card.v1.Card").getClasses();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Collection<CardLightInfo> listAllCards() {
        for (Class<?> aClass : classes) {
            System.out.println(aClass);
        }
        return Collections.emptyList();
    }

    public static void main(String... s) {
        new CardService().listAllCards();
    }

}
