package com.kuhrusty.morbadscorepad.model;

/**
 * A very small amount of information about which expansions might be in play.
 * <i>Maybe</i> we'll have Expansions include information about the content, but
 * more likely we'll just have other objects tagged with the expansion ID, and
 * filter them out of lists etc. when the expansions aren't in play.
 */
public class Expansion {
    private String id;
    private String name;  //  human-readable
    private String subdir;

    public Expansion() {
    }
    //  these guys just get built by Gson
    //public Expansion(String id, String name, String subdir) {
    //    this.id = id;
    //    this.name = name;
    //    this.subdir = subdir;
    //}

    /**
     * Returns the ID which should uniquely identify this expansion.
     */
    public String getID() {
        return id;
    }

    /**
     * Returns the human-readable name of this expansion.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name of the subdirectory (under the config directory's
     * "expansions" directory) containing this expansion's files.
     */
    public String getSubDir() {
        return subdir;
    }
}
