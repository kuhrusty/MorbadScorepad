package com.kuhrusty.morbadscorepad.model;

import android.content.Context;

import com.kuhrusty.morbadscorepad.TestUtil;
import com.kuhrusty.morbadscorepad.model.dao.CachingGameRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository2;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MissionTest {
    private Context context;
    private GameConfiguration config;
    private GameRepository grepos;
    private String dataDirectory = "HandOfDoom";

    @Before
    public void initConfig() {
        context = TestUtil.mockContext();
        grepos = new CachingGameRepository(new JSONGameRepository2());
        config = new GameConfiguration(context, dataDirectory, grepos, "wb_wetlands");
    }

    @Test
    public void testGetMissions() throws Exception {
        List<Mission> missions = grepos.getMissions(context, config);
        assertNotNull(missions);
        assertEquals(24, missions.size());
        assertEquals("On the Run", missions.get(0).getName());
        assertEquals("Bringer of Doom", missions.get(19).getName());
        assertNull(missions.get(19).getExpansionID());
        assertEquals("Serve the Scroglin King", missions.get(20).getName());
        assertEquals("Rise of the Megaverm", missions.get(23).getName());
        assertEquals("wb_wetlands", missions.get(23).getExpansionID());

//now check prev/next

    }
}