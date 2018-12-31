package com.kuhrusty.morbadscorepad.model.dao;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kuhrusty.morbadscorepad.model.AdventurerSheet;
import com.kuhrusty.morbadscorepad.model.Card;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.Expansion;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.Map;
import com.kuhrusty.morbadscorepad.model.Mission;

import java.util.List;

/**
 * This is a wrapper around another GameRepository implementation; it caches the
 * results and only calls the wrapped version when the configuration changes.
 */
public class CachingGameRepository implements GameRepository {
    private static final String LOGBIT = "CachingGameRepository";

    private GameConfiguration cacheConfig;
    private int cacheModCount;
    //   cached stuff
    private List<AdventurerSheet> adventurers;
    private List<Expansion> expansions;
    private List<Mission> missions;
    private java.util.Map<String, Deck> decks;
//    private TreeMap<String, List<? extends Card>> cards = new TreeMap<>();
    private Map map;

    private GameRepository wrapped;

    /**
     * The repository which we'll use when the requested result isn't already in
     * the cache.  Must not be null.
     */
    public CachingGameRepository(GameRepository repos) {
        wrapped = repos;
    }

    @Override
    public List<AdventurerSheet> getAdventurerSheets(Context context, GameConfiguration config) {
        if (!cacheIsValid(config, adventurers)) {
            adventurers = wrapped.getAdventurerSheets(context, config);
        } else {
            Log.d(LOGBIT, "returning " + adventurers.size() + " cached Adventurers");
        }
        return adventurers;
    }

    @Override
    public List<Expansion> getExpansions(Context context, GameConfiguration config) {
        if (!cacheIsValid(config, expansions)) {
            expansions = wrapped.getExpansions(context, config);
        } else {
            Log.d(LOGBIT, "returning " + expansions.size() + " cached Expansions");
        }
        return expansions;
    }

    @Override
    public List<Mission> getMissions(Context context, GameConfiguration config) {
        if (!cacheIsValid(config, missions)) {
            missions = wrapped.getMissions(context, config);
        } else {
            Log.d(LOGBIT, "returning " + missions.size() + " cached Missions");
        }
        return missions;
    }

    @Override
    public java.util.Map<String, Deck> getDecks(Context context, GameConfiguration config) {
        if (!cacheIsValid(config, decks)) {
            decks = wrapped.getDecks(context, config);
        } else {
            Log.d(LOGBIT, "returning " + decks.size() + " cached Decks");
        }
        return decks;
    }

    @Override
    public <T extends Card> Deck<T> getDeck(Context context, GameConfiguration config, String deckID, Class<T> tClass) {
        java.util.Map<String, Deck> tm = getDecks(context, config);
        if (tm != null) {
            return (Deck<T>)(tm.get(deckID));  //  well, this is suspicious...
        }
        return null;
    }

    @Override
    public <T extends Card> List<T> getCards(Context context, GameConfiguration config, Deck<T> deck, Class<T> tClass) {
Log.w(LOGBIT, "getCards() doesn't actually cache");
return wrapped.getCards(context, config, deck, tClass);
    }

    @Override
    public <T extends Card> List<T> getCards(Context context, GameConfiguration config, String deckID, Class<T> tClass) {
        Deck<T> deck = getDeck(context, config, deckID, tClass);
        return getCards(context, config, deck, tClass);
    }

    @Override
    public Map getMap(Context context, GameConfiguration config) {
        if (!cacheIsValid(config, map)) {
            map = wrapped.getMap(context, config);
        } else {
            Log.d(LOGBIT, "returning cached Map");
        }
        return map;
    }

    /**
     * Returns true if the given cached object is still valid, false if it's
     * null or the game configuration has changed since we last loaded cached
     * stuff.  In that case, this has the side effect of clearing all cached
     * stuff.
     */
    private boolean cacheIsValid(GameConfiguration config, @Nullable Object cachedObject) {
        if ((cacheConfig == config) && (cacheModCount == config.getModCount())) {
            return cachedObject != null;
        }
        //  still here?  config has changed, so all cached objects are invalid.
        Log.d(LOGBIT, "clearing cache");
        adventurers = null;
        expansions = null;
        missions = null;
        decks = null;
//        cards = new TreeMap<>();
        map = null;
        cacheConfig = config;
        cacheModCount = config.getModCount();
        return false;
    }
}
