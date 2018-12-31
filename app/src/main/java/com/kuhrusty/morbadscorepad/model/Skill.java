package com.kuhrusty.morbadscorepad.model;

import com.kuhrusty.morbadscorepad.Req;
import com.kuhrusty.morbadscorepad.Requirement;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class Skill extends Card {

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
     * Returns a list which contains only the Skills which are applicable to the
     * given adventurer.
     *
     * @param allSkills the set of skills you want filtered.
     * @param adventurer may be null; in that case, <code>allSkills</code> is
     *                   returned.
     * @return A new list, or <code>allSkills</code>.
     */
    public static List<Skill> filterSkills(List<Skill> allSkills, AdventurerSheet adventurer) {
        if (adventurer == null) {
            return allSkills;
        }
        List<Skill> rv = new ArrayList<>(allSkills.size());
        for (Skill ts : allSkills) {
            if (ts.getRequirements().passes(adventurer)) rv.add(ts);
        }
        return rv;
    }

    //  Originally I was going to have the list of required classes as an array
    //  in JSON, and the "MRL 6" stuff as a separate string, but... you can
    //  probably ditch the requiredClass stuff and convert those arrays back
    //  into a single String
    private String req;
    //  transient so that it gets ignored by Gson.  This is our "compiled" req
    //  string.
    private transient Requirement<AdventurerSheet> creq;

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
