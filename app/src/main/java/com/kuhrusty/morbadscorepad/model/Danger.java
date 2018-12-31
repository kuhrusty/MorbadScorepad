package com.kuhrusty.morbadscorepad.model;

/**
 * A card in the Danger deck.  Currently danger.json has information about the
 * encounters etc., but that's being ignored.
 */
public class Danger extends Card {

    private boolean reshuffle = false;  //  set by Gson

    /**
     * True if this is a "RESHUFFLE DANGER DECK" card.
     */
    public boolean isReshuffle() {
        return reshuffle;
    }
}
