package com.kuhrusty.morbadscorepad.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.kuhrusty.morbadscorepad.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Instead of references to useful objects like Skills, Adventurers, etc.,
 * this is all Strings etc. so that it can be passed around between Activities.
XXX ugh, that's dumb, see blather in Adventurer.
 */
public class Campaign implements Parcelable {

    /**
     * Sorts by name, timestamp.
     */
    public static Comparator<Campaign> NameTimestampComparator = new Comparator<Campaign>() {
        @Override
        public int compare(Campaign cs1, Campaign cs2) {
            int rv = Util.compare(cs1.getCampaignName(), cs2.getCampaignName());
            if (rv != 0) return rv;
            if (cs1.getTimestamp() < cs2.getTimestamp()) return -1;
            if (cs1.getTimestamp() > cs2.getTimestamp()) return 1;
            return 0;
        }
    };

    private String campaignName;
    private long timestamp;
    private String id;  //  used for figuring out the file name
    private String notes;

    private int highlandDoom = 0;
    private int lowlandDoom = 0;
    private int badlandDoom = 0;
    private int wetlandDoom = 0;

    private String mission;
    private String faction;
    private List<String> doom;
    //XXX name & location of each Epic Monster counter & each Voidgate in play.
    //XXX If one of these is on a Path or Road, indicate which Locations they
    //XXX are between.
    private int bountyLevel;
    private LocationOrTerritory handOfDoom;
//  can we get the list of locations from map.json?
//    private List<LocationState> highlands;
//    private List<LocationState> lowlands;
//    private List<LocationState> badlands;
//    private List<LocationState> wetlands;
    private List<Adventurer> adventurers;
    private Map map;

    private List<Deck> decks;

    public Campaign() {
        adventurers = new ArrayList<>(4);
        for (int ii = 0; ii < 4; ++ii) adventurers.add(null);
    }
    public Campaign(String id, String campaignName, String mission, long timestamp) {
        this.id = id;
        this.campaignName = campaignName;
        this.mission = mission;
        this.timestamp = timestamp;
    }

    public String getCampaignName() {
        return campaignName;
    }
    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
    public String getMissionName() {
        return mission;
    }
    public void setMissionName(String mission) {
        this.mission = mission;
    }
    public String getDateString() {
        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a").format(new Date(timestamp));
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public int getAdventurerCount() {
        return (adventurers != null) ? adventurers.size() : 0;
    }
    public Adventurer getAdventurer(int index) {
        return adventurers.get(index);
    }
    public void setAdventurer(int index, Adventurer adventurer) {
        adventurers.set(index, adventurer);
    }
    public void addAdventurer(Adventurer adventurer) {
        for (int ii = 0; ii < adventurers.size(); ++ii) {
            if (adventurers.get(ii) == null) {
                adventurers.set(ii, adventurer);
                return;
            }
        }
        // !?  more than 4?
        adventurers.add(adventurer);
    }

    /**
     * Any location which exists on the given printed Map, and not in this
     * Campaign, will be added; any location which does exist will have
     * map traits from the printed map added.
     *
     * @param printed must not be null.
     */
    public void setPrintedMap(Map printed) {
        if (this.map == null) {
            this.map = new Map();
        }
        this.map.setPrintedMap(printed);
    }
    public Map getMap() {
        return map;
    }

    //  Parcelable stuff from here down.
    public static final Parcelable.Creator<Campaign> CREATOR
            = new Parcelable.Creator<Campaign>() {
        public Campaign createFromParcel(Parcel in) {
            return new Campaign(in);
        }
        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };
    private Campaign(Parcel in) {
        campaignName = in.readString();
        timestamp = in.readLong();
        id = in.readString();
        notes = in.readString();
        highlandDoom = in.readByte();
        lowlandDoom = in.readByte();
        badlandDoom = in.readByte();
        wetlandDoom = in.readByte();
        mission = in.readString();
        faction = in.readString();
        doom = in.createStringArrayList();
        bountyLevel = in.readByte();
        adventurers = in.createTypedArrayList(Adventurer.CREATOR);
        map = in.readParcelable(Map.class.getClassLoader());
//        private LocationOrTerritory handOfDoom;
////  can we get the list of locations from map.json?
//        private List<LocationState> highlands;
//        private List<LocationState> lowlands;
//        private List<LocationState> badlands;
//        private List<LocationState> wetlands;
//        in.create
//        private List<Adventurer> adventurers;

//        private List<Deck> decks;

    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(campaignName);
        out.writeLong(timestamp);
        out.writeString(id);
        out.writeString(notes);
        out.writeByte((byte)highlandDoom);
        out.writeByte((byte)lowlandDoom);
        out.writeByte((byte)badlandDoom);
        out.writeByte((byte)wetlandDoom);
        out.writeString(mission);
        out.writeString(faction);
        out.writeStringList(doom);
        out.writeByte((byte)bountyLevel);
        out.writeTypedList(adventurers);
        out.writeParcelable(map, flags);
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
