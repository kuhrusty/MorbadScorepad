package com.kuhrusty.morbadscorepad.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A very small amount of information about which expansions might be in play.
 * <i>Maybe</i> we'll have Expansions include information about the content, but
 * more likely we'll just have other objects tagged with the expansion ID, and
 * filter them out of lists etc. when the expansions aren't in play.
 */
public class Expansion implements Parcelable {
    private String id;
    private String name;  //  human-readable
    private String subdir;

    public Expansion() {
    }

    /**
     * These guys normally get built by Gson; this is for use in unit tests.
     * @param id
     * @param name
     * @param subdir
     */
    public Expansion(String id, String name, String subdir) {
        this.id = id;
        this.name = name;
        this.subdir = subdir;
    }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(subdir);
    }
    public static final Parcelable.Creator<Expansion> CREATOR =
            new Parcelable.Creator<Expansion>() {
                public Expansion createFromParcel(Parcel in) {
                    Expansion rv = new Expansion();
                    rv.id = in.readString();
                    rv.name = in.readString();
                    rv.subdir = in.readString();
                    return rv;
                }
                public Expansion[] newArray(int size) {
                    return new Expansion[size];
                }
            };
    @Override
    public int describeContents() {
        return 0;
    }
}
