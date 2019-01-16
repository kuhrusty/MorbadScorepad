package com.kuhrusty.morbadscorepad.model;

import android.content.Context;

import com.kuhrusty.mockparcel.MockParcel;
import com.kuhrusty.morbadscorepad.TestUtil;
import com.kuhrusty.morbadscorepad.model.dao.CachingGameRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameConfigurationTest {

    private Context context;
    private GameRepository grepos;

    @Before
    public void before() {
        context = TestUtil.mockContext();
        grepos = new CachingGameRepository(new JSONGameRepository2());
    }

    /**
     * Confirms that these guys survive Parcel.
     */
    @Test
    public void testParcel() {
        GameConfiguration gc1 = new GameConfiguration(context, "HandOfDoom",
                grepos, "hod_ks", "cp1");
        gc1.setDataDirectory("flerp");
        gc1.setDataDirectory("snorkel");
        assertEquals(2, gc1.getModCount());
        assertEquals("snorkel", gc1.getDataDirectory());
        gc1.setDataDirectory("HandOfDoom");
        assertEquals(3, gc1.getModCount());
        assertEquals("HandOfDoom", gc1.getDataDirectory());
        assertTrue(gc1.hasExpansion("hod_ks"));
        assertTrue(gc1.hasExpansion("cp1"));
        assertFalse(gc1.hasExpansion("cp2"));

        GameConfiguration gc2 = MockParcel.parcel(gc1, GameConfiguration.CREATOR);
        assertTrue(gc1 != gc2);
        assertEquals(3, gc2.getModCount());
        assertEquals("HandOfDoom", gc2.getDataDirectory());
        assertTrue(gc2.hasExpansion("hod_ks"));
        assertTrue(gc2.hasExpansion("cp1"));
        assertFalse(gc2.hasExpansion("cp2"));
   }
}
