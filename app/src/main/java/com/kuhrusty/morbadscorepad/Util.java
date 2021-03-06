package com.kuhrusty.morbadscorepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.kuhrusty.morbadscorepad.model.GameConfiguration;

import java.util.Collection;

public class Util {

    private static ThreadLocal<Context> tlContext = new ThreadLocal<>();
    private static ThreadLocal<GameConfiguration> tlConfig = new ThreadLocal<>();

    /**
     * Sets thread-local Context and GameConfiguration which may be used during
     * de-parceling or whatever.  You probably want to use this like this:
     *
     * <pre>
     *     try {
     *         Util.setContextAndConfig(context, config);
     *         //  call some method which is going to need access to the context
     *         //  and/or config, but whose API prevents it from being passed in
     *         //  directly
     *     } finally {
     *         Util.setContextAndConfig(null, null);
     *     }
     * </pre>
     *
     * @param context may be null.
     * @param config may be null.
     */
    public static void setContextAndConfig(Context context, GameConfiguration config) {
        if (context != null) {
            tlContext.set(context);
        } else {
            tlContext.remove();
        }
        if (config != null) {
            tlConfig.set(config);
        } else {
            tlConfig.remove();
        }
    }

    /**
     * Returns the thread-local Context last passed to setContextAndConfig(), or
     * null.
     */
    public static Context getContext() {
        return tlContext.get();
    }
    /**
     * Returns the thread-local GameConfiguration last passed to
     * setContextAndConfig(), or null.
     */
    public static GameConfiguration getConfig() {
        return tlConfig.get();
    }

    /**
     * Pretty sure <i>this</i> bit of code has only been written two million
     * times.  Compares two strings, treating null as less than "".  Returns
     * &lt; 0 if s1 is before s2, &gt; 0 if s2 is before s1, and 0 if they're
     * equal.
     */
    public static int compare(@Nullable String s1, @Nullable String s2) {
        if (s1 == s2) {
            return 0;
        } else if ((s1 == null) && (s2 != null)) {
            return -1;
        } else if ((s1 != null) && (s2 == null)) {
            return 1;
        }
        return s1.compareTo(s2);
    }

    /**
     * And this bit of code has only been written 1.5 million times.  If
     * conjunction is "or", this should give you:
     * <pre>
     * foo -&gt; "foo"
     * foo bar -&gt; "foo or bar"
     * foo bar baz -&gt; "foo, bar, or baz"
     * </pre>
     *
     *  If conjunction is null, this should give you:
     * <pre>
     * foo -&gt; "foo"
     * foo bar -&gt; "foo, bar"
     * foo bar baz -&gt; "foo, bar, baz"
     * </pre>
     */
    public static String oxfordComma(String conjuction, Object... list) {
        StringBuilder rv = new StringBuilder();
        for (int ii = 0; ii < (list.length - 1); ++ii) {
            if (ii > 0) rv.append(", ");
            rv.append(list[ii]);
        }
        if (list.length > 1) {
            if (conjuction == null) {
                rv.append(", ");
            } else {
                if (list.length > 2) rv.append(",");
                rv.append(" ").append(conjuction).append(" ");
            }
        }
        rv.append(list[list.length - 1]);
        return rv.toString();
    }

//Arghh, requires API level 24.
//    /**
//     * Another chunk of code which must already exist.  Linear search, return
//     * the first element for which the test returns true.
//     */
//    public static <T> T find(Collection<T> list, Predicate<T> test) {
//        for (T tt : list) {
//            if (test.test(tt)) return tt;
//        }
//        return null;
//    }
    /**
     * Another chunk of code which must already exist.  Linear search, return
     * the first element for which the test returns true.
     */
    public static <T> T find(Collection<T> list, Requirement<T> test) {
        for (T tt : list) {
            if (test.passes(tt)) return tt;
        }
        return null;
    }

    /**
     * <i>This</i> exists... just in API level 26.  Sheesh.
     */
    public static String join(String delimiter, Iterable it) {
        StringBuilder buf = new StringBuilder();
        for (Object obj : it) {
            if (buf.length() > 0) buf.append(delimiter);
            buf.append(obj.toString());
        }
        return buf.toString();
    }

    /**
     * Returns true if the pref_developer preference is true.
     *
     * @param context must not be null.
     */
    public static boolean isDeveloper(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs != null) && prefs.getBoolean("pref_developer", false);
     }
}
