package com.kuhrusty.morbadscorepad.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * This keeps an adventurer's current state.
 *
 * <p>Instead of references to useful objects like Skills, Adventurers, etc.,
 * this is all Strings etc. so that it can be passed around between Activities.
<pre>
XXX that's dumb; you should keep references to real objects, and just write their
XXX ids in writeToParcel(), and get new references back out of the static
XXX RepositoryFactory when reading from parcel.
</pre>
 */
public class Adventurer implements Parcelable {
    private String name;
    private String character;
    private String space;
    private String notes;

    private int wounds;
    private int gp;
    private int xp;
    private int luck;

    /**
     * For all of these, these Strings are either card IDs, or names which
     * someone entered manually.
     */
    private List<String> skills = new ArrayList<>(8);
    private List<String> mastery = new ArrayList<>(8);
    private List<String> weaknesses = new ArrayList<>(2);
    private List<String> triumphs = new ArrayList<>(2);
    private List<String> loot = new ArrayList<>();

    public Adventurer() {
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }
    public void setCharacter(String character) {
        this.character = character;
    }

    public String getSpace() {
        return space;
    }
    public void setSpace(String space) {
        this.space = space;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    //  Parcelable stuff from here down.
    public static final Parcelable.Creator<Adventurer> CREATOR
            = new Parcelable.Creator<Adventurer>() {
        public Adventurer createFromParcel(Parcel in) {
            return new Adventurer(in);
        }

        public Adventurer[] newArray(int size) {
            return new Adventurer[size];
        }
    };
    private Adventurer(Parcel in) {
        name = in.readString();
        character = in.readString();
        space = in.readString();
        wounds = in.readInt();
        gp = in.readInt();
        xp = in.readInt();
        luck = in.readInt();
        in.readStringList(skills);
        in.readStringList(mastery);
        in.readStringList(weaknesses);
        in.readStringList(triumphs);
        in.readStringList(loot);
    }
    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(name);
        out.writeString(character);
        out.writeString(space);
        out.writeInt(wounds);
        out.writeInt(gp);
        out.writeInt(xp);
        out.writeInt(luck);
        out.writeStringList(skills);
        out.writeStringList(mastery);
        out.writeStringList(weaknesses);
        out.writeStringList(triumphs);
        out.writeStringList(loot);
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
