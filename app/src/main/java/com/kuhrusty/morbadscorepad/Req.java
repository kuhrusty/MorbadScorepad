package com.kuhrusty.morbadscorepad;

import com.kuhrusty.morbadscorepad.model.AdventurerSheet;

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
     * This passes Adventurers who have one of the given classes.  This also
     * treats the adventurer's name as a class, as the Voidwalker skill requires
     * "Void Witch."
     */
    public static class ClassRequirement extends Req {
        private final String[] classes;

        /**
         * @param classes must not be null, must not contain any nulls, and must
         *                contain at least one element.  And don't screw with
         *                its contents after passing it in.
         */
        public ClassRequirement(String... classes) {
            this.classes = classes;
            if ((classes == null) || (classes.length == 0)) {
                throw new IllegalArgumentException();
                //  not checking for nulls in the array...
            }
        }
        @Override
        public boolean passes(AdventurerSheet adventurer) {
            String[] aa = adventurer.getClasses();
//            if ((aa == null) || (aa.length == 0)) return false;
            for (int ii = 0; ii < classes.length; ++ii) {
                String tc = classes[ii];
                for (int jj = 0; (aa != null) && (jj < aa.length); ++jj) {
                    if (tc.equals(aa[jj])) {
                        return true;
                    }
                }
                if (tc.equals(adventurer.getName())) return true;
            }
            return false;
        }
        @Override
        public String toString() {
            return Util.oxfordComma("or", (Object[])classes);
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
    //  well, this could be better...
    private static final Pattern classSplitter =
            Pattern.compile("(?:\\s*,\\s*or\\s+|\\s*,\\s*|\\s+or\\s+)", Pattern.CASE_INSENSITIVE);

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
        //  Well, this is pretty crude.  If it starts with a stat name (AGI,
        //  CON, etc.), let's assume it's stat-based; otherwise we're going to
        //  treat it as a list of classes.
        req = req.trim();
        Matcher tm = startsWithStat.matcher(req);
        if (tm.find()) {
            boolean isOr = true;
            String[] bits = orSplitter.split(req);
            if (bits.length == 1) {
                bits = andSplitter.split(req);
                isOr = false;
            }
            if (bits.length == 1) {
                return parseOneStat(bits[0]);
            } else {
                Requirement<AdventurerSheet> ta[] = new Requirement[bits.length];
                for (int ii = 0; ii < bits.length; ++ii) {
                    ta[ii] = parseOneStat(bits[ii]);
                }
                return isOr ? new Requirement.Or<AdventurerSheet>(ta) :
                              new Requirement.And<AdventurerSheet>(ta);
            }
        } else {
            return new ClassRequirement(classSplitter.split(req));
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
