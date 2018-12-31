package com.kuhrusty.morbadscorepad.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MapTraitTest {
    @Test
    public void testFlagBits() throws Exception {
        assertEquals(1, MapTrait.Base.getFlagBit());
        assertEquals(2, MapTrait.Destroyed.getFlagBit());
        //  whoo, pretty exhaustive set of tests there.  Take a break; you've
        //  earned it!
    }
}
