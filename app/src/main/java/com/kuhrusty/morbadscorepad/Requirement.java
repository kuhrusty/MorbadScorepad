package com.kuhrusty.morbadscorepad;

import android.support.annotation.NonNull;

/**
 * Intended for determining whether an Adventurer meets the requirements for a
 * skill, item, etc.  In this case, <tt>T</tt> would be Adventurer.
 *
 * <p>I probably should've used java.util.function.Predicate instead of this.
 */
public interface Requirement<T> {
    /**
     * Returns true if the given object meets this Requirement.
     */
    boolean passes(T t);

    abstract class BranchRequirement<T> implements Requirement<T> {
        private final Requirement<T>[] branches;
        private final boolean or;  //  true if this is or, false if it's and
        /**
         * Don't mess with <tt>branches</tt> once it's passed in!
         */
        public BranchRequirement(boolean or, @NonNull Requirement<T>... branches) {
            this.or = or;
            this.branches = branches;
        }
        /**
         * Returns true if any of the branches passed to the constructor return
         * true.
         */
        @Override
        public boolean passes(T t) {
            for (int ii = 0; ii < branches.length; ++ii) {
                //  maybe too clever for my own good: if or is true, and the
                //  child returns true, then we return true; if or is false
                //  (meaning we're "and"), and the child returns false, we
                //  return false.  Ah, well, if it's broken, we'll find out in
                //  unit testing.
                if (branches[ii].passes(t) == or) return or;
            }
            return !or;
        }
        @Override
        public String toString() {
            return Util.oxfordComma(or ? "or" : "and", (Object[])branches);
        }
    }
    class And<T> extends BranchRequirement<T> {
        public And(@NonNull Requirement<T>... branches) {
            super(false, branches);
        }
    }
    class Or<T> extends BranchRequirement<T> {
        public Or(@NonNull Requirement<T>... branches) {
            super(true, branches);
        }
    }
}
