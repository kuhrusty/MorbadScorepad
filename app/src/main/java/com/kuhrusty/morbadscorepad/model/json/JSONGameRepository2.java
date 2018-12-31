package com.kuhrusty.morbadscorepad.model.json;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kuhrusty.morbadscorepad.model.Expandable;
import com.kuhrusty.morbadscorepad.model.Expansion;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This uses one directory per expansion; instead of having a single file
 * containing the list of all adventurers, it scans subdirectories for each
 * expansion's adventurers file.  This is more complicated, but it means new
 * expansions can be added without fiddling with base game files.
 */
public class JSONGameRepository2 extends JSONGameRepository {
    private static final String LOGBIT = "JSONGameRepository2";

    /**
     * The subdirectory containing expansion subdirectories.  For example, if
     * our data directory is "HandOfDoom", and the expansion in question has
     * "CharacterPack1" as its subdirectory, then its characters will be in
     * HandOfDoom/expansions/CharacterPack1/adventurers.json.
     */
    public static final String EXPANSIONS_DIRECTORY_NAME = "expansions";

//  Ehhh... I wanted to scan the tree, but for now, let's continue to read the
//  list of Expansions from a single file, which is way less cool.  Blah.
//    /**
//     * This is overridden to scan the subdirectories in the data directory,
//     * looking for EXPANSION_ID_FILE_NAME files; when it finds them, it adds
//     * them to the list.
//     */
//    @Override
//    public List<Expansion> getExpansions(GameConfiguration config) {
//        Log.wtf(LOGBIT, "need to override getExpansions() to not call getList()");
//    }

    /**
     * Overridden to call the superclass method once per expansion, substituting
     * the expansion's subdirectory name into the file name, and then assembling
     * the results into a single list.
     */
    @Override
    <T> List<T> getList(Context context, GameConfiguration config, String filename,
                        Class<T> expectedClass, boolean filterExpansions,
                        @Nullable Comparator<T> sort) {
        //  we need to leave the base class handling of Expansions alone.
        if (expectedClass == Expansion.class) {
            return super.getList(context, config, filename, expectedClass,
                    filterExpansions, sort);
        }
        List<Expansion> expansions = getExpansions(context, config);

        //  Get the base game stuff.  We pass filterExpansions = false and
        //  sort = null because we're going to do the filtering & sorting
        //  after adding expansion stuff.
        List<T> rv = super.getList(context, config, filename, expectedClass, false, null);
        //  OK if rv is null
        if (rv != null) {
            Log.d(LOGBIT, filename + ": loaded " + rv.size() + " base game elements");
        }

        for (Expansion te : expansions) {
            if (filterExpansions && (!config.hasExpansion(te.getID()))) continue;
            //  Again, we pass filterExpansions = false and sort = null because
            //  we're going to do the filtering & sorting afterward.
            List<T> tl = super.getList(context, config,
                    EXPANSIONS_DIRECTORY_NAME + "/" + te.getSubDir() + "/" + filename,
                    expectedClass, false, null);
            if (tl != null) {
                //  Run through all Expandable elements and set their expansion
                //  ID.
                for (T tt : tl) {
                    if (tt instanceof Expandable) {
                        ((Expandable)tt).setExpansionID(te.getID());
                    }
                }
                if (rv == null) {
                    rv = tl;
                } else {
                    rv.addAll(tl);
                }
                if (Log.isLoggable(LOGBIT, Log.DEBUG)) {
                    Log.d(LOGBIT, filename + ": added " + tl.size() + " " +
                            te.getID() + " elements");
                }
            }
        }

        if (rv != null) {
            //  we don't filter expansions here because we did it above, when
            //  we decided whether or not to read the expansion's file.
            //if (filterExpansions) filterExpansions(config, rv);
            if (sort != null) Collections.sort(rv, sort);
            if (Log.isLoggable(LOGBIT, Log.DEBUG)) {
                Log.d(LOGBIT, filename + ": returning " + rv.size() +
                        " elements");
            }
        }
        return rv;
    }
}
