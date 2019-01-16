package com.kuhrusty.morbadscorepad.model;

import com.kuhrusty.mockparcel.MockParcel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExpansionTest {
    /**
     * Confirms that these guys survive Parcel.
     */
    @Test
    public void testParcel() {
        Expansion e1 = new Expansion("cp666", "Character Pack 666", "SomeSubdir");
        assertEquals("cp666", e1.getID());
        assertEquals("Character Pack 666", e1.getName());
        assertEquals("SomeSubdir", e1.getSubDir());

        Expansion e2 = MockParcel.parcel(e1, Expansion.CREATOR);
        assertTrue(e1 != e2);
        assertEquals(e1.getID(), e2.getID());
        assertEquals(e1.getName(), e2.getName());
        assertEquals(e1.getSubDir(), e2.getSubDir());
   }
}
