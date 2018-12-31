package com.kuhrusty.morbadscorepad.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Quite possibly the dumbest name I've ever given a class; probably should've
 * gone with "Board."
 */
public class Map implements Parcelable {
    private List<Region> regions;
    private transient HashMap<String, LocationState> locationMap;

    public Map() { }
//    public Map(Map that) {
//        copyRegions(that);
//    }

    private void copyRegions(Map that) {
        if (that.regions != null) {
            this.regions = new ArrayList<>(that.regions.size());
            for (int ii = 0; ii < that.regions.size(); ++ii) {
                this.regions.add(new Region(that.regions.get(ii)));
            }
        }
    }

    /**
     * This Map now owns the List.  May be null.
     */
    public Map(List<Region> regions) {
        this.regions = regions;
    }

    /**
     * Any location which exists on the given printed Map, and not in this Map,
     * will be added; any location which does exist will have map traits from
     * the printed map added.
     *
     * @param printed must not be null.
     */
    public void setPrintedMap(Map printed) {
        if (regions == null) {
            copyRegions(printed);
            return;
        }
        for (int pri = 0; pri < printed.getRegions().size(); ++pri) {
            Region pr = printed.getRegions().get(pri);
            boolean foundRegion = false;
            for (int tri = pri; tri < regions.size(); ++tri) {
                Region tr = regions.get(tri);
                if (pr.getName().equals(tr.getName())) {
                    foundRegion = true;
                    for (int pli = 0; pli < pr.getSize(); ++pli) {
                        LocationState pl = pr.getLocationOrTerritory(pli);
                        boolean foundLocation = false;
                        for (int tli = pli; tli < tr.getSize(); ++tli) {
                            LocationState tl = tr.getLocationOrTerritory(tli);
                            if (pl.getName().equals(tl.getName())) {
                                foundLocation = true;
                                tl.setPrintedLocation(pl);
                                break;
                            }
                        }
                        if (!foundLocation) {
                            tr.addLocationOrTerritory(pli, new LocationState(pl));
                        }
                    }
                    break;
                }
            }
            if (!foundRegion) {
                regions.add(pri, new Region(pr));
            }
        }
        locationMap = null;  //  in case it's stale
    }

    public LocationState getLocationOrTerritory(String name) {
        if (regions == null) return null;
        //  the lists are so tiny, hashing them probably doesn't save us any
        //  time...
        if (locationMap == null) {
            HashMap<String, LocationState> tm = new HashMap<>();
            for (Region tr : regions) {
                for (int ii = 0; ii < tr.getSize(); ++ii) {
                    LocationState tl = tr.getLocationOrTerritory(ii);
                    tm.put(tl.getName(), tl);
                }
            }
            locationMap = tm;
        }
        return locationMap.get(name);
    }

    /**
     * "Please don't modify the contents of the list."
     *
     * @return the list of Regions; may be null.
     */
    public List<Region> getRegions() {
        return regions;
    }

    //  Parcelable stuff from here down.
    public static final Creator<Map> CREATOR
            = new Creator<Map>() {
        public Map createFromParcel(Parcel in) {
            return new Map(in);
        }
        public Map[] newArray(int size) {
            return new Map[size];
        }
    };
    private Map(Parcel in) {
        regions = in.createTypedArrayList(Region.CREATOR);
    }
    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeTypedList(regions);
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
