package com.kuhrusty.morbadscorepad.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.kuhrusty.morbadscorepad.model.dao.GameRepository;

import java.util.HashMap;
import java.util.List;

/**
 * Information about which expansions etc. are in play.
 */
public class GameConfiguration implements Parcelable {
    private int modcount = 0;
    private String dataDirectory;
    private String expansionPrefKeyPrefix;
    //  key is expansion ID
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
     * This one is used by CREATOR.
     */
    private GameConfiguration() {
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

    /**
     * Note that this guy writes complete Expansion instances, and creates new
     * ones when de-parceling, rather than getting shared instances from
     * GameRepository; that may be a bad idea.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(modcount);
        dest.writeString(dataDirectory);
        dest.writeString(expansionPrefKeyPrefix);
        if (expansions != null) {
            dest.writeInt(expansions.size());
            for (Expansion te : expansions.values()) {
                dest.writeParcelable(te, 0);
            }
        } else {
            dest.writeInt(0);
        }
    }
    public static final Parcelable.Creator<GameConfiguration> CREATOR =
            new Parcelable.Creator<GameConfiguration>() {
                public GameConfiguration createFromParcel(Parcel in) {
                    GameConfiguration rv = new GameConfiguration();
                    rv.modcount = in.readInt();
                    rv.dataDirectory = in.readString();
                    rv.expansionPrefKeyPrefix = in.readString();
                    int xcount = in.readInt();
                    while (xcount-- > 0) {
                        if (rv.expansions == null) {
                            rv.expansions = new HashMap<>();
                        }
                        Expansion te = in.readParcelable(Expansion.class.getClassLoader());
                        rv.expansions.put(te.getID(), te);
                    }
                    return rv;
                }
                public GameConfiguration[] newArray(int size) {
                    return new GameConfiguration[size];
                }
            };
    @Override
    public int describeContents() {
        return 0;
    }
}
