package com.kuhrusty.morbadscorepad.model.dao.json;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.kuhrusty.morbadscorepad.TestUtil;
import com.kuhrusty.morbadscorepad.model.Campaign;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.Map;
import com.kuhrusty.morbadscorepad.model.MapTrait;
import com.kuhrusty.morbadscorepad.model.dao.CachingGameRepository;
import com.kuhrusty.morbadscorepad.model.dao.CampaignRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.dao.RepositoryFactory;
import com.kuhrusty.morbadscorepad.model.json.JSONCampaignRepository;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static com.kuhrusty.morbadscorepad.TestUtil.compare;

/**
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ContextCompat.class, Environment.class})
public class JSONCampaignRepositoryTest {

    private Context context;
    private GameConfiguration config;
    private GameRepository grepos;
    private CampaignRepository crepos;

    @Before
    public void initConfig() {
        context = TestUtil.mockContext();
        grepos = new CachingGameRepository(new JSONGameRepository2());
        crepos = new JSONCampaignRepository();
        RepositoryFactory.setGameRepository(grepos);
        RepositoryFactory.setCampaignRepository(crepos);
        config = new GameConfiguration(context, "HandOfDoom", grepos, "cp1");

        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.mockStatic(Environment.class);
        PowerMockito.when(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS)).thenReturn(new File("app/build/tmp/"));
   }

    @Before
    public void cleanTempFiles() {
        File dir = new File("app/build/tmp/HandOfDoom");
        if (!dir.exists()) return;
        for (String fn : dir.list()) {
            System.err.println("deleting " + fn);
            File tf = new File(dir, fn);
            assertTrue(tf.delete());
        }
    }

    @Test
    public void testWriteCampaign() throws Exception {
        Map map = grepos.getMap(context, config);
        Campaign cs = new Campaign(null, "Punchy Brothers", "On the Run", 1234567890000L);
        cs.setPrintedMap(map);
        crepos.write(config, cs);
        compare("HandOfDoom/campaign1.json", "app/build/tmp/HandOfDoom/save1234567890.json",
                true, false);
        compare("HandOfDoom/index1.json", "app/build/tmp/HandOfDoom/campaignIndex.txt",
                false, false);

        cs = new Campaign(null, "Punchy Brothers", "Death for Hire", 1234571490000L);
        cs.setPrintedMap(map);
        cs.getMap().getLocationOrTerritory("Tomb Lake").setDangerLevel(5);
        cs.getMap().getLocationOrTerritory("Stone Circle").setTownLevel(1);
        cs.getMap().getLocationOrTerritory("The Black Tree").setDangerLevel(6);
        cs.getMap().getLocationOrTerritory("The Black Tree").addTrait(MapTrait.Fungus);
        cs.getMap().getLocationOrTerritory("The Black Tree").addTrait(MapTrait.Perilous);
        crepos.write(config, cs);
        compare("HandOfDoom/campaign2.json", "app/build/tmp/HandOfDoom/save1234571490.json",
                true, false);
        compare("HandOfDoom/index2.json", "app/build/tmp/HandOfDoom/campaignIndex.txt",
                false, false);

        //  Well, let's test reads, too.
        cs = crepos.read(context, config, "save1234571490");
        assertNotNull(cs);
        assertEquals(5, cs.getMap().getLocationOrTerritory("Tomb Lake").getDangerLevel());
        assertEquals(1, cs.getMap().getLocationOrTerritory("Stone Circle").getTownLevel());
        assertEquals(6, cs.getMap().getLocationOrTerritory("The Black Tree").getDangerLevel());
        assertTrue(cs.getMap().getLocationOrTerritory("The Black Tree").hasTrait(MapTrait.Fungus));
        assertTrue(cs.getMap().getLocationOrTerritory("The Black Tree").hasTrait(MapTrait.Perilous));
        assertTrue(cs.getMap().getLocationOrTerritory("The Black Tree").hasTrait(MapTrait.Outside));
        //  stuff from the printed map
        assertTrue(cs.getMap().getLocationOrTerritory("The Black Tree").hasTrait(MapTrait.Woodland));
        assertFalse(cs.getMap().getLocationOrTerritory("The Black Tree").hasTrait(MapTrait.Settlement));
        assertEquals(4, cs.getMap().getLocationOrTerritory("Skull Bridge").getDangerLevel());
        assertTrue(cs.getMap().getLocationOrTerritory("Skull Bridge").hasTrait(MapTrait.Inside));
        assertFalse(cs.getMap().getLocationOrTerritory("Skull Bridge").hasTrait(MapTrait.Perilous));
    }
}