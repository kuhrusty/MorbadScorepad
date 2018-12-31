package com.kuhrusty.morbadscorepad.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.kuhrusty.morbadscorepad.model.dao.GameRepository;

import java.util.HashMap;
import java.util.List;

/**
 * Information about which expansions etc. are in play.
 */
public class GameConfiguration {
    private int modcount = 0;
    private String dataDirectory;
    private String expansionPrefKeyPrefix;
    private HashMap<String, Expansion> expansions;

    public String expansionIDToPrefKey(String expansionID) {
        return expansionPrefKeyPrefix + expansionID;
    }
    public String prefKeyToExpansionID(String prefKey) {
        //  even though isExpansionPrefKey() may have returned true, this may
        //  not be a pref key for an expansion in *this* game, in which case
        //  figuring out the expansion ID is hard.
        return prefKey.startsWith(expansionPrefKeyPrefix) ?
                prefKey.substring(expansionPrefKeyPrefix.length()) : null;
    }
    public static boolean isExpansionPrefKey(String prefKey) {
        return prefKey.startsWith("pref_expansion_");
    }

    private void updateExpansionPrefKeyPrefix() {
        expansionPrefKeyPrefix = "pref_expansion_" + dataDirectory.replaceAll("[./]", "_") + "_";
    }

    public GameConfiguration(Context context, SharedPreferences prefs, GameRepository grepos) {
        dataDirectory = prefs.getString("pref_data_dir", "HandOfDoom");
        updateExpansionPrefKeyPrefix();
        List<Expansion> allExpansions = grepos.getExpansions(context,this);
        if (allExpansions != null) {
            for (String key : prefs.getAll().keySet()) {
//XXX probably need to catch ClassCastException here in case there's a
//XXX non-boolean pref... but in that case we're hosed
                String expansionID;
                if (isExpansionPrefKey(key) && prefs.getBoolean(key, false) &&
                        ((expansionID = prefKeyToExpansionID(key)) != null)) {
                    Expansion expansion = null;
                    for (Expansion te : allExpansions) {
                        if (expansionID.equals(te.getID())) {
                            expansion = te;
                            break;
                        }
                    }
                    if (expansion != null) {
                        if (expansions == null) expansions = new HashMap<>();
                        expansions.put(expansionID, expansion);
                    }
                }
            }
        }
    }

    /**
     * For creating bogus configurations in unit tests.
     */
    public GameConfiguration(Context context, String dataDirectory,
                             GameRepository grepos, String... expansionIDs) {
        this.dataDirectory = dataDirectory;
        updateExpansionPrefKeyPrefix();
        if ((expansionIDs == null) || (expansionIDs.length == 0)) return;
        List<Expansion> allExpansions = grepos.getExpansions(context, this);
        if (allExpansions != null) {
            for (String expansionID : expansionIDs) {
                Expansion expansion = null;
                for (Expansion te : allExpansions) {
                    if (expansionID.equals(te.getID())) {
                        expansion = te;
                        break;
                    }
                }
                if (expansion != null) {
                    if (expansions == null) expansions = new HashMap<>();
                    expansions.put(expansionID, expansion);
                }
            }
        }
    }

    /**
     * This value gets incremented whenever the configuration changes.
     */
    public int getModCount() { return modcount; }

    /**
     * The directory (in CLASSPATH or assets) containing the various data files
     * about missions, locations, etc. in the game.  If you want to experiment
     * with different sets of stuff, or your own maps, etc., then change this to
     * something else, and then put your own versions of missions.json etc.
     * in the directory.
     *
     * @return either the data directory, or an empty string, never null.
     */
    public String getDataDirectory() {
        return (dataDirectory != null) ? dataDirectory : "";
    }

    public void setDataDirectory(String dataDirectory) {
        if (dataDirectory != null) {
            this.dataDirectory = dataDirectory;
            updateExpansionPrefKeyPrefix();
            ++modcount;
        }
    }

    /**
     * Returns true if the expansion with the given ID is in use by the user.
     */
    public boolean hasExpansion(String expansionID) {
        return (expansions != null) && (expansions.containsKey(expansionID));
    }
}
