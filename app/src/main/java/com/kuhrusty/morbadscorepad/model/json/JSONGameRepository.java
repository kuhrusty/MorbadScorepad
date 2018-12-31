package com.kuhrusty.morbadscorepad.model.json;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.kuhrusty.morbadscorepad.model.AdventurerSheet;
import com.kuhrusty.morbadscorepad.model.Card;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.Expandable;
import com.kuhrusty.morbadscorepad.model.Expansion;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.LocationState;
import com.kuhrusty.morbadscorepad.model.Map;
import com.kuhrusty.morbadscorepad.model.Mission;
import com.kuhrusty.morbadscorepad.model.Region;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This uses a single directory containing a single file of adventurers, a
 * single file of loot cards, etc., and a file containing the list of
 * expansion IDs.
 */
public class JSONGameRepository implements GameRepository {
    private static final String LOGBIT = "JSONGameRepository";

    public static final String ADVENTURER_FILE_NAME = "adventurers.json";
    public static final String EXPANSION_FILE_NAME = "expansions.json";
    public static final String MISSION_FILE_NAME = "missions.json";
    public static final String DECK_FILE_NAME = "decks.json";
    public static final String MAP_FILE_NAME = "map.json";

    /**
     * Originally this loaded its files from CLASSPATH, because I did not know
     * the Android assets directory was a thing.
     */
    private boolean useClasspath = false;

    /**
     * Returns a new GsonBuilder with some type adapters registered.
     */
    public static GsonBuilder newGsonBuilder() {
        GsonBuilder rv = new GsonBuilder();
        rv.registerTypeAdapter(LocationState.class, LocationState.PRETTY_JSON);
        rv.registerTypeAdapter(Region.class, Region.PRETTY_JSON);
        return rv;
    }

    @Override
    public List<AdventurerSheet> getAdventurerSheets(Context context,
                                                     GameConfiguration config) {
        return getList(context, config, ADVENTURER_FILE_NAME, AdventurerSheet.class,
                true, AdventurerSheet.NameComparator);
    }

    @Override
    public List<Expansion> getExpansions(Context context, GameConfiguration config) {
        return getList(context, config, EXPANSION_FILE_NAME, Expansion.class, false, null);
    }

    @Override
    public List<Mission> getMissions(Context context, GameConfiguration config) {
        return getList(context, config, MISSION_FILE_NAME, Mission.class, true, null);
    }

    @Override
    public java.util.Map<String, Deck> getDecks(Context context, GameConfiguration config) {
        List<Deck> decks = getList(context, config, DECK_FILE_NAME, Deck.class, true, null);
        if (decks == null) return null;
        HashMap<String, Deck> rv = new HashMap<>();
        for (Deck td : decks) {
            rv.put(td.getID(), td);
        }
        return rv;
    }

    @Override
    public <T extends Card> Deck<T> getDeck(Context context, GameConfiguration config,
                                            String deckID, Class<T> tClass) {
        List<Deck> decks = getList(context, config, DECK_FILE_NAME, Deck.class, true, null);
        if (decks == null) return null;
        //  linear search, but should only be a couple dozen elements
        for (Deck td : decks) {
//you know, you really need to add T.class on the Deck so that you can confirm that it matches here.
            if (deckID.equals(td.getID())) return td;
        }
        return null;
    }

    @Override
    public <T extends Card> List<T> getCards(Context context, GameConfiguration config,
                                             String deckID, Class<T> tClass) {
        Deck<T> deck = getDeck(context, config, deckID, tClass);
        if (deck == null) {
            Log.e(LOGBIT, "getDeck(config, \"" + deckID + "\") returned null!");
            return null;
        }
        return getCards(context, config, deck, tClass);
    }

    @Override
    public <T extends Card> List<T> getCards(Context context, GameConfiguration config,
                                             Deck<T> deck, Class<T> tClass) {
        String filename = deck.getCardFileName();
        List<T> rv = getList(context, config, filename, tClass, true, null);
        //  cache them on the deck itself... bad idea?
        deck.setCards(rv);
        return rv;
    }

    @Override
    public Map getMap(Context context, GameConfiguration config) {
        return new Map(getList(context, config, MAP_FILE_NAME, Region.class,
                true, null));
    }

    /**
     * Reads elements of the given class from a JSON file which is expected to
     * contain only an array.
     *
     * @param filename will be looked for in CLASSPATH.
     * @param expectedClass the class of objects we expect to find in the file.
     * @param filterExpansions if true, elements which implement Expandable
     *                         whose expansion ID isn't in the current game
     *                         configuration will be filtered out of the list.
     * @param sort if not null, this will be used for sorting the list before
     *             returning it.
     * @param <T> the type of object we expect to find in the file.
     */
    <T> List<T> getList(Context context, GameConfiguration config,
                        String filename, Class<T> expectedClass,
                        boolean filterExpansions, @Nullable Comparator<T> sort) {
        String fullPath = config.getDataDirectory() + "/" + filename;
        InputStream is;
//Log.d(LOGBIT, "about to open \"" + fullPath + "\", useClasspath == " + useClasspath);
        if (useClasspath) {
            is = getClass().getClassLoader().getResourceAsStream(fullPath);
            if (is == null) {
                //  Not necessarily an error; JSONGameRepository2 calls this on
                //  files which may not exist.
                Log.w(LOGBIT, "failed to find " + fullPath + " in CLASSPATH");
                return null;
            }
        } else {
            try {
                is = context.getAssets().open(fullPath);
            } catch (IOException e) {
                //  Not necessarily an error; JSONGameRepository2 calls this on
                //  files which may not exist.
                Log.w(LOGBIT, "failed to find " + fullPath + " in assets");
                return null;
            }
        }
        Gson gson = newGsonBuilder().create();
        JsonReader reader = null;
        List<T> rv = new ArrayList<T>();
        try {
            reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            reader.setLenient(true);  //  so we can have embedded comments
            reader.beginArray();
            while (reader.hasNext()) {
                T te = gson.fromJson(reader, expectedClass);
                rv.add(te);
            }
            reader.endArray();
        } catch (IOException ioe) {
            Log.e(LOGBIT, "failed to read " + filename, ioe);
            return null;
        } finally {
            try {
                if (reader != null) reader.close();
                else if (is != null) is.close();
            } catch (IOException ioe) {
                Log.e(LOGBIT, "failed to close " + filename, ioe);
            }
        }
        if (filterExpansions) filterExpansions(config, rv);
        if (sort != null) Collections.sort(rv, sort);
        return rv;
    }

    /**
     * Removes elements from the given list if they implement Expandable and
     * their expansion ID isn't in the GameConfiguration.
     */
    private void filterExpansions(GameConfiguration config, List list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            String id;
            if ((obj instanceof Expandable) &&
                ((id = ((Expandable)obj).getExpansionID()) != null) &&
                (!config.hasExpansion(id))) {
                it.remove();
            }
        }
    }
}
