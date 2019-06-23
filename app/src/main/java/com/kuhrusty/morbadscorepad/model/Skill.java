package com.kuhrusty.morbadscorepad.model;

import android.util.Log;

import com.kuhrusty.morbadscorepad.Req;
import com.kuhrusty.morbadscorepad.Requirement;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class Skill extends Card {

    public Skill() {
    }
    public Skill(String id, String name) {
        super(id, name);
    }

    /**
     * For determining whether a given Skill matches a given name.
     */
    public static class NameRequirement implements Requirement<Skill> {
        private final String lookingFor;
        public NameRequirement(String lookingFor) {
            this.lookingFor = lookingFor;
        }
        @Override
        public boolean passes(Skill skill) {
            return (skill != null) && lookingFor.equals(skill.getName());
        }
    };

    /**
     * Passes skills which require Fighter, Hunter, Merchant, Militant,
     * Performer, or Rogue.
     */
    public static final Requirement<Skill> DilettanteRequirement = new Requirement<Skill>() {
        @Override
        public boolean passes(Skill skill) {
            if ((skill == null) || skill.getID().equals("dilettante")) return false;
            Requirement<AdventurerSheet> reqs = skill.getRequirements();
            if (reqs.equals(Req.Passes)) return false;
            if (reqs instanceof Req.ClassRequirement) {
                Req.ClassRequirement cr = (Req.ClassRequirement)reqs;
                //  suspicious hard-coding
                return cr.contains("Fighter") ||
                       cr.contains("Hunter") ||
                       cr.contains("Merchant") ||
                       cr.contains("Militant") ||
                       cr.contains("Performer") ||
                       cr.contains("Rogue");
            } else if (reqs instanceof Req.StatRequirement) {
                return false;
            } else if (reqs instanceof Requirement.BranchRequirement) {
                //  What this *should* do is recurse through the branches of
                //  this reqs' tree.  However, currently, none of the and/or
                //  requirements are affected by Dilettante; if new skills are
                //  added for which that's not the case, the unit test failure
                //  "should" cause someone to fix this.
                return false;
            } else {
                Log.e("DilettanteRequirement",
                        "not handling requirement \"" + reqs + "\"");
                return false;
            }
        }
    };

    /**
     * Returns a list which contains only the Skills which are applicable to the
     * given adventurer.
     *
     * @param allSkills the set of skills you want filtered.
     * @param adventurer may be null; in that case, <code>allSkills</code> is
     *                   returned.
     * @return A new list, or <code>allSkills</code>.
     */
    public static List<Skill> filterSkills(List<Skill> allSkills, AdventurerSheet adventurer) {
        return filterSkills(allSkills, adventurer, 0);
    }

    /**
     * Returns a list which contains only the Skills which are applicable to the
     * given adventurer.
     *
     * @param allSkills the set of skills you want filtered.
     * @param adventurer may be null; in that case, <code>allSkills</code> is
     *                   returned.
     * @param xp the XP limit, or 0 for no limit.  If not 0, then only skills
     *           which cost this amount or less will be returned.
     * @return A new list, or <code>allSkills</code>.
     */
    public static List<Skill> filterSkills(List<Skill> allSkills, AdventurerSheet adventurer, int xp) {
        if (adventurer == null) {
            if (xp == 0) return allSkills;
            List<Skill> rv = new ArrayList<>(allSkills.size());
            for (Skill ts : allSkills) {
                if (xp >= ts.xp) rv.add(ts);
            }
            return rv;
        }
        List<Skill> rv = new ArrayList<>(allSkills.size());
        for (Skill ts : allSkills) {
            if (ts.getRequirements().passes(adventurer)) {
                if ((xp == 0) || (xp >= ts.xp)) rv.add(ts);
            }
        }
        return rv;
    }

    //  Originally I was going to have the list of required classes as an array
    //  in JSON, and the "MRL 6" stuff as a separate string, but... you can
    //  probably ditch the requiredClass stuff and convert those arrays back
    //  into a single String
    private String req;
    private int xp;
    //  transient so that it gets ignored by Gson.  This is our "compiled" req
    //  string.
    private transient Requirement<AdventurerSheet> creq;

    /**
     * The experience points required for gaining or mastering this skill.
     */
    public int getXP() { return xp; }

    /**
     * Returns the Requirements an adventurer must meet to have this skill; if
     * this skill has no requirements, this returns Requirement.Passes, not null.
     */
    public Requirement<AdventurerSheet> getRequirements() {
        if (creq == null) {
            if (req != null) {
                creq = Req.parse(req);
            } else {
                creq = Req.Passes;
            }
        }
        return creq;
    }
}
