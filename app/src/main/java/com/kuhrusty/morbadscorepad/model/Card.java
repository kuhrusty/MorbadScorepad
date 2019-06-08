package com.kuhrusty.morbadscorepad.model;

import java.util.Comparator;

/**
 * A base class for cards in the game.
 */
public class Card implements Expandable {
    private String expansionID;
    //private Deck deck;
    private String name;
    private String id;

    public static Comparator<Card> NameComparator = new Comparator<Card>() {
        @Override
        public int compare(Card c1, Card c2) {
            String c1s = c1.getName();
            String c2s = c2.getName();
            if ((c1s == null) || (c2s == null)) return 0;
            //  ugh, not localizable
            if (c1s.startsWith("The ")) c1s = c1s.substring(4);
            if (c2s.startsWith("The ")) c2s = c2s.substring(4);
            return c1s.compareTo(c2s);
        }
    };

    /**
     * Note that, uhh... "ID" is a bit misleading, as there may be duplicates
     * in a given deck.  This may turn out to be a terrible idea.
     */
    public String getID() {
        return id;
    }
    public String getName() {
        return name;
    }

    @Override
    public String getExpansionID() {
        return expansionID;
    }
    @Override
    public void setExpansionID(String id) {
        expansionID = id;
    }
}
