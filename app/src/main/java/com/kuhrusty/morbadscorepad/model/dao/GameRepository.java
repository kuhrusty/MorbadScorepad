package com.kuhrusty.morbadscorepad.model.dao;

import android.content.Context;

import com.kuhrusty.morbadscorepad.model.AdventurerSheet;
import com.kuhrusty.morbadscorepad.model.Card;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.Expansion;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.Map;
import com.kuhrusty.morbadscorepad.model.Mission;

import java.util.List;

/**
 * For loading static information about the AdventurerSheets, Expansions, etc.
 * in a game.  This is the stuff which shouldn't change every time someone saves
 * their current adventurer or campaign state; however, it <i>may</i> change
 * when someone changes which expansions are enabled.
 */
public interface GameRepository {
    /**
     * Returns a list of all AdventurerSheet objects in the configured set of
     * expansions.
     *
     * @param context should probably not be null.
     * @param config must not be null.
     * @return the list, in alphabetical order, or null if there was an error.
     */
    List<AdventurerSheet> getAdventurerSheets(Context context,
                                              GameConfiguration config);

    /**
     * Returns a list of all Expansion objects (not just the ones the
     * GameConfiguration says are currently in use).
     *
     * @param context should probably not be null.
     * @param config must not be null.
     * @return the list, in the order they should be displayed, or null if there
     *         was an error.
     */
    List<Expansion> getExpansions(Context context, GameConfiguration config);

    /**
     * Returns a list of all Mission objects in the configured set of expansions.
     *
     * @param context should probably not be null.
     * @param config must not be null.
     * @return the list, in the order they should be displayed, or null if there
     *         was an error.
     */
    List<Mission> getMissions(Context context, GameConfiguration config);

    java.util.Map<String, Deck> getDecks(Context context, GameConfiguration config);
    <T extends Card> Deck<T> getDeck(Context context, GameConfiguration config, String deckID, Class<T> tClass);

    <T extends Card> List<T> getCards(Context context, GameConfiguration config, Deck<T> deck, Class<T> tClass);
    <T extends Card> List<T> getCards(Context context, GameConfiguration config, String deckID, Class<T> tClass);

    Map getMap(Context context, GameConfiguration config);
}
