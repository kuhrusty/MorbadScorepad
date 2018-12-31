package com.kuhrusty.morbadscorepad.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LocationStateTest {

    @Test
    public void testMapTraits() throws Exception {
        LocationState ls = new LocationState("Bandit Camp", 4, 0);
        ls.addTrait(MapTrait.Outside);
        ls.addTrait(MapTrait.Settlement);
        assertTrue(ls.hasTrait(MapTrait.Outside));
        assertTrue(ls.hasTrait(MapTrait.Settlement));
        assertFalse(ls.hasTrait(MapTrait.Inside));
        assertFalse(ls.hasTrait(MapTrait.Explored));
        assertEquals(4, ls.getDangerLevel());
        assertEquals(0, ls.getTownLevel());
        ls.setTownLevel(4);
        assertEquals(0, ls.getDangerLevel());
        assertEquals(4, ls.getTownLevel());

        assertFalse(ls.hasTrait(MapTrait.Fungus));
        ls.addTrait(MapTrait.Fungus);
        assertTrue(ls.hasTrait(MapTrait.Fungus));
        ls.removeTrait(MapTrait.Fungus);
        assertFalse(ls.hasTrait(MapTrait.Fungus));
        assertTrue(ls.hasTrait(MapTrait.Outside));
        assertTrue(ls.hasTrait(MapTrait.Settlement));
        assertFalse(ls.hasTrait(MapTrait.Inside));
        assertFalse(ls.hasTrait(MapTrait.Explored));
    }
}