package com.kuhrusty.morbadscorepad.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * As in the other state classes, this contains Strings etc. instead of
 * references to real, useful objects so that it can be shuffled around
 * between activities without grief.
XXX ugh, that's dumb, see blather in Adventurer.
 */
public class LocationState implements Parcelable {

    private String name;
    //  negative means town level
    private int danger;
    //  bit flags.
    private int traits;
    //  transient so it... err, well, we've got a custom JsonSerializer anyway.
    //  The deal is, in the "printed" map (the one which is the same in every
    //  game), this is null; in the Campaign map, this is a reference to
    //  the corresponding printed-map-location.  When we go to write
//XXX derp, sometimes I like to complete
    private transient LocationState printedLocation;

    //    private Set<String> traits;
    public boolean hasTrait(MapTrait trait) {
        return (traits & trait.getFlagBit()) != 0;
    }
    public void addTrait(MapTrait trait) {
        traits |= trait.getFlagBit();
    }
    public void removeTrait(MapTrait trait) {
        traits &= (~trait.getFlagBit());
    }
    public void setPrintedLocation(LocationState other) {
        if (danger == 0) danger = other.danger;
        traits |= other.traits;
        printedLocation = other;
    }
    public boolean differsFromPrinted() {
        return (printedLocation == null) ||
               (danger != printedLocation.danger) ||
               (traits != printedLocation.traits);
    }
    public boolean traitsDifferFromPrinted() {
        return (printedLocation == null) || (traits != printedLocation.traits);
    }

    public LocationState() {
    }

    /**
     * @param name
     * @param danger negative for town level
     * @param traitFlags
     */
    public LocationState(String name, int danger, int traitFlags) {
        this.name = name;
        this.danger = danger;
        this.traits = traitFlags;
    }

    /**
     * This is not just a copy constructor!  It also sets <code>that</code> as
     * the printed state.
     *
     * @param that must not be null, and its printedState must be null.
     */
    public LocationState(LocationState that) {
        name = that.name;
        danger = that.danger;
        traits = that.traits;
        if (that.printedLocation != null) {
            throw new IllegalArgumentException("non-printed location being used as printed location!?");
        }
        printedLocation = that;
    }

    public String getName() {
        return name;
    }

    /**
     * If this location has a town level instead of a danger level, this returns
     * 0.
     */
    public int getDangerLevel() {
        return (danger > 0) ? danger : 0;
    }
    public void setDangerLevel(int level) {
        danger = level;
    }
    public int getPrintedDangerLevel() {
        return (printedLocation != null) ? printedLocation.getDangerLevel() : getDangerLevel();
    }

    /**
     * If this location has a danger level instead of a town level, this returns
     * 0.
     */
    public int getTownLevel() {
        return (danger < 0) ? -danger : 0;
    }
    public void setTownLevel(int level) {
        danger = -level;
    }
    public int getPrintedTownLevel() {
        return (printedLocation != null) ? printedLocation.getTownLevel() : getTownLevel();
    }

    /**
     * This guy splits up the danger level & the town level, and explodes the
     * set of map traits into an array of strings.  This makes it pretty, but
     * bloated.  Err, but, to reduce the bloat, we filter out the properties
     * which are the same as the printed location, if any.
     */
    public static class JsonPrettifier implements JsonSerializer<LocationState>, JsonDeserializer<LocationState> {
        @Override
        public JsonElement serialize(LocationState src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject rv = new JsonObject();
            rv.addProperty("name", src.name);
            if ((src.printedLocation == null) || (src.printedLocation.danger != src.danger)) {
                if (src.danger > 0) {
                    rv.addProperty("danger", src.danger);
                } else if (src.danger < 0) {
                    rv.addProperty("town", -src.danger);
                }
            }
            if ((src.printedLocation == null) || (src.printedLocation.traits != src.traits)) {
            //if ((src.traits & MapTrait.RECORD_IN_CAMPAIGN_MASK) != 0) {
                JsonArray ta = new JsonArray();
                for (int ii = 0; ii < MapTrait.values().length; ++ii) {
                    MapTrait tt = MapTrait.values()[ii];
                    if (tt.recordInCampaign && src.hasTrait(tt) &&
                        ((src.printedLocation == null) || (!src.printedLocation.hasTrait(tt)))) {
                        ta.add(tt.toString());
                    }
                }
                if (ta.size() > 0) rv.add("traits", ta);
            }
            return rv;
        }

        @Override
        public LocationState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String name = obj.get("name").getAsString();
            JsonElement te = null;

            int danger = 0;
            if ((te = obj.get("danger")) != null) {
                danger = te.getAsInt();
            } else if ((te = obj.get("town")) != null) {
                danger = -te.getAsInt();
            }

            int traits = 0;
            if ((te = obj.get("traits")) != null) {
                JsonArray ta = te.getAsJsonArray();
                for (int ii = 0; ii < ta.size(); ++ii) {
                    MapTrait tt = MapTrait.valueOf(ta.get(ii).getAsString());
                    if (tt != null) {
                        traits |= tt.getFlagBit();
                    }
                }
            }

            return new LocationState(name, danger, traits);
        }
    }
    public static final JsonPrettifier PRETTY_JSON = new JsonPrettifier();

    //  Parcelable stuff from here down.
    public static final Parcelable.Creator<LocationState> CREATOR
            = new Parcelable.Creator<LocationState>() {
        public LocationState createFromParcel(Parcel in) {
            return new LocationState(in);
        }
        public LocationState[] newArray(int size) {
            return new LocationState[size];
        }
    };
    private LocationState(Parcel in) {
        name = in.readString();
        danger = in.readByte();
        traits = in.readInt();
        byte tb = in.readByte();
        if (tb != 0) printedLocation = in.readParcelable(LocationState.class.getClassLoader());
    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeByte((byte)danger);
        out.writeInt(traits);
        if (printedLocation != null) {
            out.writeByte((byte)1);
            out.writeParcelable(printedLocation, flags);
        } else {
            out.writeByte((byte)0);
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
