package com.kuhrusty.morbadscorepad.model;

import com.kuhrusty.morbadscorepad.Requirement;

import java.util.Comparator;

/**
 * The base information about an adventurer.  This doesn't track their current
 * stats; it's just the stuff printed on the adventurer's sheet.
 */
public class AdventurerSheet implements Expandable {
    private String expansionID;
    private String name;
    private String[] classes;
    private int agi;
    private int con;
    private int mag;
    private int mrl;
    private int per;
    private int str;

    public enum Stat {
        AGI, CON, MAG, MRL, PER, STR
    }

    public static Comparator<AdventurerSheet> NameComparator = new Comparator<AdventurerSheet>() {
        @Override
        public int compare(AdventurerSheet a1, AdventurerSheet a2) {
            return a1.name.compareTo(a2.name);
        }
    };

    /**
     * For determining whether a given adventurer matches a given name.
     */
    public static class NameRequirement implements Requirement<AdventurerSheet> {
        private final String lookingFor;
        public NameRequirement(String lookingFor) {
            this.lookingFor = lookingFor;
        }
        @Override
        public boolean passes(AdventurerSheet adventurer) {
            return (adventurer != null) && lookingFor.equals(adventurer.getName());
        }
    };

    @Override
    public String getExpansionID() {
        return expansionID;
    }
    @Override
    public void setExpansionID(String id) {
        expansionID = id;
    }

    public String getName() {
        return name;
    }
    public String[] getClasses() {
        return classes;
    }

    public int getStatValue(Stat stat) {
        switch (stat) {
            case AGI: return agi;
            case CON: return con;
            case MAG: return mag;
            case MRL: return mrl;
            case PER: return per;
            case STR: return str;
        }
        throw new RuntimeException("unhandled stat " + stat);  //  real classy
    }
}
