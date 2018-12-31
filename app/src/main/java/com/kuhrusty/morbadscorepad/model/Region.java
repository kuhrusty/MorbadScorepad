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
import java.util.ArrayList;
import java.util.List;

/**
 * Elsewhere, we've got AdventurerSheet, which is the static stuff common to all
 * games, and Adventurer, which is the stuff which changes during play; here, we
 * seem to have both the printed map stuff and the changeable token stuff in
 * the same class.
 */
public class Region implements Parcelable {
    private String name;
    //  ermm... was going to use LocationOrTerritory here...
    private List<LocationState> locations;

    public Region() { }
    public Region(Region that) {
        this.name = that.name;
        if (that.locations != null) {
            this.locations = new ArrayList<>(that.locations.size());
            for (int ii = 0; ii < that.locations.size(); ++ii) {
                this.locations.add(new LocationState(that.locations.get(ii)));
            }
        }
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the number of locations + territories
     */
    public int getSize() {
        return (locations != null) ? locations.size() : 0;
    }

    public LocationState getLocationOrTerritory(int ii) {
        return locations.get(ii);
    }
    public LocationState getLocationOrTerritory(String name) {
        //  ugh, linear search
        for (int ii = 0; ii < locations.size(); ++ii) {
            //LocationOrTerritory tt = locations.get(ii);
            LocationState tt = locations.get(ii);
            if (tt.getName().equals(name)) return tt;
        }
        return null;
    }

    /**
     * @param pos where in the list; first element is 0.
     * @param ls must not be null.
     */
    public void addLocationOrTerritory(int pos, LocationState ls) {
        //  the 12 here is because the Lowlands currently have the most, at 11.
        if (locations == null) locations = new ArrayList<>(12);
        locations.add(pos, ls);
    }

    /**
     * This guy just filters out locations which claim to be the same as their
     * printed-on-the-map locations.
     */
    public static class JsonPrettifier implements JsonSerializer<Region> {//}, JsonDeserializer<LocationState> {
        @Override
        public JsonElement serialize(Region src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject rv = new JsonObject();
            if (src.name != null) rv.addProperty("name", src.name);
            if (src.locations != null) {
                JsonArray ta = null;
                for (int ii = 0; ii < src.locations.size(); ++ii) {
                    LocationState tl = src.locations.get(ii);
                    if (tl.differsFromPrinted()) {
                        if (ta == null) ta = new JsonArray();
                        ta.add(context.serialize(tl, LocationState.class));
                    }
                }
                if (ta != null) {
                    rv.add("locations", ta);
                }
            }
            return rv;
        }

//        @Override
//        public LocationState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            JsonObject obj = json.getAsJsonObject();
//            String name = obj.get("name").getAsString();
//            JsonElement te = null;
//
//            int danger = 0;
//            if ((te = obj.get("danger")) != null) {
//                danger = te.getAsInt();
//            } else if ((te = obj.get("town")) != null) {
//                danger = -te.getAsInt();
//            }
//
//            int traits = 0;
//            if ((te = obj.get("traits")) != null) {
//                JsonArray ta = te.getAsJsonArray();
//                for (int ii = 0; ii < ta.size(); ++ii) {
//                    MapTrait tt = MapTrait.valueOf(ta.get(ii).getAsString());
//                    if (tt != null) {
//                        traits |= tt.getFlagBit();
//                    }
//                }
//            }
//
//            return new LocationState(name, danger, traits);
//        }
    }
    public static final JsonPrettifier PRETTY_JSON = new JsonPrettifier();

    //  Parcelable stuff from here down.
    public static final Parcelable.Creator<Region> CREATOR
            = new Parcelable.Creator<Region>() {
        public Region createFromParcel(Parcel in) {
            return new Region(in);
        }
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };
    private Region(Parcel in) {
        name = in.readString();
        locations = in.createTypedArrayList(LocationState.CREATOR);
    }
    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(name);
        out.writeTypedList(locations);
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
