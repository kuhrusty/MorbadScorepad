package com.kuhrusty.morbadscorepad.model;

import android.support.annotation.Nullable;

/**
 * Things which have an expansion ID.  A class being Expandable means some
 * instances of that class might have come from an expansion or promo.
 */
public interface Expandable {
    /**
     * Returns the expansion ID this thing is from, or null if it came in the
     * base game.
     */
    @Nullable String getExpansionID();

    /**
     * Sets the expansion ID; you probably never want to call this.
     */
    void setExpansionID(@Nullable String id);
}
