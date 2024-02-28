package com.kuhrusty.morbadscorepad;

import com.kuhrusty.morbadscorepad.model.AdventurerSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Well, this is a sad/stupid name for this class, but I'm cranky because I
 * couldn't put static methods in the Adventurer interface (well, when it
 * <i>was</i> an interface instead of a class) without changing my API level.  I
 * wanted to say Requirement.parse() rather than AdventureRequirement.parse()!
 * Adventurer.Requirement.parse() might have been OK...
 */
public abstract class Req implements Requirement<AdventurerSheet> {

    /**
     * For skills etc. which have no requirements.
     */
    public static final Req Passes = new Req() {
        @Override
        public boolean passes(AdventurerSheet ignored) {
            return true;
        }

//        /**
//         * Ehh, so we can identify this guy in unit tests.
//         */
//        @Override
//        public String toString() { return "(None)"; }
    };

    /**
     * This passes Adventurers who have the given class.  This also treats the
     * adventurer's name as a class, as the Voidwalker skill requires "Void Witch."
     */
    public static class ClassRequirement extends Req {
        private final String className;

        /**
         * @param className must not be null.
         */
        public ClassRequirement(String className) {
            this.className = className;
            if (className == null) {
                throw new IllegalArgumentException();
            }
        }
        @Override
        public boolean passes(AdventurerSheet adventurer) {
            String[] aa = adventurer.getClasses();
            for (int ii = 0; (aa != null) && (ii < aa.length); ++ii) {
                if (className.equals(aa[ii])) {
                    return true;
                }
            }
            return className.equals(adventurer.getName());
        }

        /**
         * Returns true if the given className is the class required by this
         * skill (used by Skill.DilettanteRequirement).
         */
        public boolean contains(String className) {
            return this.className.equals(className);
        }

        @Override
        public String toString() {
            return className;
        }
    }

    /**
     * This passes Adventurers whose given stat is high or low enough.
     */
    public static class StatRequirement extends Req {
        private final AdventurerSheet.Stat stat;
        private final int target;
        private final boolean lessThan;

        public StatRequirement(AdventurerSheet.Stat stat, int target, boolean lessThan) {
            this.stat = stat;
            this.target = target;
            this.lessThan = lessThan;
        }
        @Override
        public boolean passes(AdventurerSheet adventurer) {
            return lessThan ? (adventurer.getStatValue(stat) < target) :
                              (adventurer.getStatValue(stat) >= target);
        }
        //  for unit tests
        @Override
        public String toString() {
            return stat + (lessThan ? " <" : " ") + target;
        }
    }

    private static final Pattern startsWithStat =
            Pattern.compile("^(?:AGI|CON|MAG|MRL|PER|STR)\\s");//, Pattern.CASE_INSENSITIVE);
    private static final Pattern orSplitter =
            Pattern.compile("\\s+or\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern andSplitter =
            Pattern.compile("\\s+and\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern singleStat =
            Pattern.compile("^(AGI|CON|MAG|MRL|PER|STR)\\s+(<)?\\s*(\\d+)$");//, Pattern.CASE_INSENSITIVE);
    private static final Pattern andOrSplitter =
            Pattern.compile("(?:\\s*,\\s+(?:and|or)|\\s*,|\\s+(?:and|or))\\s+", Pattern.CASE_INSENSITIVE);

    /**
     * Currently this looks for stuff like "MRL 6", "MRL 6 or STR <9", or a list
     * of classes.  If you get more complicated than that, you're improving the
     * code below.
     *
     * @return well... in the case where there are no requirements, this returns
     *         Requirement.Passes rather than null, so that you never have to
     *         check for nulls.  I... may regret that.  In fact I kind of do
     *         already.
     */
    public static Requirement<AdventurerSheet> parse(String req) {
        req = req.trim();
        List<Requirement<AdventurerSheet>> ta = new ArrayList<>(4);
        String[] bits = andOrSplitter.split(req);
        for (String bit : bits) {
            if (startsWithStat.matcher(bit).find()) {
                ta.add(parseOneStat(bit));
            } else {
                ta.add(new ClassRequirement(bit));
            }
        }
        if (orSplitter.matcher(req).find()) {
            return new Requirement.Or<>(ta);
        } else if (andSplitter.matcher(req).find()) {
            return new Requirement.And<>(ta);
        } else if (ta.size() == 1) {
            return ta.get(0);
        } else {
            //  "Rogue, Wild, Wizard" is OR?
            return new Requirement.Or<>(ta);
            //throw new IllegalArgumentException("couldn't parse \"" + req + "\"");
        }
    }

    private static Requirement<AdventurerSheet> parseOneStat(String req) {
        Matcher tm = singleStat.matcher(req);
        if (!tm.find()) {
            //  bad, but theoretically we'll parse all the skills during unit
            //  tests?  err, except for expansion skills which are filtered out...
            throw new RuntimeException("couldn't parse \"" + req + "\"!");
        }
        //  if this misses, we'll get an IllegalArgumentException on group 1 or
        //  a NumberFormatException on group 3
        return new StatRequirement(AdventurerSheet.Stat.valueOf(tm.group(1)),
                Integer.parseInt(tm.group(3)), tm.group(2) != null);
    }
}
