package com.kuhrusty.morbadscorepad.model;

import java.util.List;

/**
 * The set of cards in a deck, and information about the deck, but not the
 * current <i>order</i> of the cards, or whether they're in the discard or draw
 * pile; for that, see {@link DeckState}.
 */
public class Deck<T extends Card> {
    public static final String DANGER = "danger";
    public static final String SKILL = "skill";

    private String id;
    private String name;
    private String cardFileName;
    private String cardClassName;
    private boolean noDiscardPile;
    //  would prefer to do this with a set of arbitrary flags read from file,
    //  rather than hardcoding these attributes here, but...
    private boolean monster;
    private boolean encounter;
    private boolean loot;
    private boolean epic;

    //  card back image
    //  size

    //private DeckType type;
    private List<T> cards;

    /**
     * May return null.
     */
    public List<T> getCards() {
        return cards;
    }
    public void setCards(List<T> cards) {
        this.cards = cards;
    }
//    private int labelResID;

//    Deck(int labelResID) {
//        this.labelResID = labelResID;
//    }

//    public String getLabel(Resources res) {
//        return type.getLabel(res);
//    }
    public String getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCardFileName() {
        return cardFileName;
    }
    public String getCardClassName() {
        return cardClassName;
    }
    public boolean isNoDiscardPile() {
        return noDiscardPile;
    }
    public boolean isMonster() {
        return monster;
    }
    public boolean isEncounter() {
        return encounter;
    }
    public boolean isLoot() {
        return loot;
    }
    public boolean isEpic() {
        return epic;
    }
}
