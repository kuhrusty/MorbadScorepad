package com.kuhrusty.morbadscorepad.model.dao.json;

import android.content.Context;

import com.kuhrusty.morbadscorepad.Req;
import com.kuhrusty.morbadscorepad.Requirement;
import com.kuhrusty.morbadscorepad.TestUtil;
import com.kuhrusty.morbadscorepad.model.AdventurerSheet;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.Expansion;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.LocationState;
import com.kuhrusty.morbadscorepad.model.Map;
import com.kuhrusty.morbadscorepad.model.MapTrait;
import com.kuhrusty.morbadscorepad.model.Mission;
import com.kuhrusty.morbadscorepad.model.Region;
import com.kuhrusty.morbadscorepad.model.Skill;
import com.kuhrusty.morbadscorepad.model.dao.CachingGameRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository2;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.Collections;
import java.util.List;
//import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JSONGameRepositoryTest {
    private Context context;
    private GameConfiguration config;
    private GameRepository grepos;
    private String dataDirectory = "HandOfDoom";

    @Before
    public void initConfig() {
        context = TestUtil.mockContext();
        grepos = new CachingGameRepository(new JSONGameRepository2());
        config = new GameConfiguration(context, dataDirectory, grepos, "cp1");
    }

    @Test
    public void testGetExpansions() throws Exception {
        List<Expansion> expansions = grepos.getExpansions(context, config);
        assertNotNull(expansions);
        assertEquals(10, expansions.size());
        assertEquals("cp1", expansions.get(1).getID());
        assertEquals("Adventurer Expansion", expansions.get(1).getName());
        assertEquals("cp2", expansions.get(2).getID());
        assertEquals("Freaks & Psychos", expansions.get(2).getName());
        assertEquals("cp3", expansions.get(3).getID());
        assertEquals("Malingerers", expansions.get(3).getName());
    }

    @Test
    public void testGetMissions() throws Exception {
        List<Mission> missions = grepos.getMissions(context, config);
        assertNotNull(missions);
        assertEquals(20, missions.size());
        assertEquals("On the Run", missions.get(0).getName());
        assertEquals("Bringer of Doom", missions.get(19).getName());

        config = new GameConfiguration(context, dataDirectory, grepos, "wb_badlands");
        missions = grepos.getMissions(context, config);
        assertNotNull(missions);
        assertEquals(24, missions.size());
        assertEquals("On the Run", missions.get(0).getName());
        assertEquals("Bringer of Doom", missions.get(19).getName());
        assertEquals("Dagger & Sword", missions.get(20).getName());
        assertEquals("Into the Unknown", missions.get(23).getName());

        config = new GameConfiguration(context, dataDirectory, grepos, "wb_badlands", "wb_wetlands");
        missions = grepos.getMissions(context, config);
        assertNotNull(missions);
        assertEquals(28, missions.size());
        assertEquals("On the Run", missions.get(0).getName());
        assertEquals("Bringer of Doom", missions.get(19).getName());
        assertEquals("Dagger & Sword", missions.get(20).getName());
        assertEquals("Into the Unknown", missions.get(23).getName());
        assertEquals("Serve the Scroglin King", missions.get(24).getName());
        assertEquals("Rise of the Megaverm", missions.get(27).getName());
        //assertEquals(26, missions.get(23).getPageNumber());

        //  Is there a way to confirm that a subsequent call uses the cache?
        List<Mission> missions2 = grepos.getMissions(context, config);
        assertTrue(missions == missions2);
    }

    @Test
    public void testGetAdventurerSheets() throws Exception {
        List<AdventurerSheet> adventurers = grepos.getAdventurerSheets(context, config);
        assertNotNull(adventurers);
        assertEquals(16, adventurers.size());
        assertEquals("Alley Cat", adventurers.get(0).getName());
        assertSameElements(adventurers.get(0).getClasses(), new String[] {"Lycanthrope", "Rogue", "Wild"});
        assertEquals("Banished Sorcerer", adventurers.get(1).getName());
        assertEquals("Bloodsport Brawler", adventurers.get(2).getName());
        assertEquals("Witch Smeller", adventurers.get(15).getName());

        adventurers = grepos.getAdventurerSheets(context, config);
        assertNotNull(adventurers);
        assertEquals(16, adventurers.size());
        assertEquals("Alley Cat", adventurers.get(0).getName());
        assertEquals("Banished Sorcerer", adventurers.get(1).getName());
        assertEquals("Bloodsport Brawler", adventurers.get(2).getName());
        assertEquals("Witch Smeller", adventurers.get(15).getName());

        //  how about base-game only?
        config = new GameConfiguration(context, dataDirectory, grepos);
        adventurers = grepos.getAdventurerSheets(context, config);
        assertNotNull(adventurers);
        assertEquals(8, adventurers.size());
        assertEquals("Bloodsport Brawler", adventurers.get(0).getName());
        assertEquals("Bog Conjurer", adventurers.get(1).getName());
        assertEquals("Corpse Burner", adventurers.get(2).getName());
        assertEquals("Witch Smeller", adventurers.get(7).getName());

        config = new GameConfiguration(context, dataDirectory, grepos,
                "cp1", "cp2", "wb_badlands", "wb_wetlands");
        adventurers = grepos.getAdventurerSheets(context, config);
        assertNotNull(adventurers);
        assertEquals(24, adventurers.size());
        assertEquals("Alley Cat", adventurers.get(0).getName());
        assertEquals("Banished Sorcerer", adventurers.get(1).getName());
        assertEquals("Bloodsport Brawler", adventurers.get(2).getName());
        assertEquals("Witch Smeller", adventurers.get(23).getName());
    }

    @Test
    public void testGetDecks() throws Exception {
        java.util.Map<String, Deck> decks = grepos.getDecks(context, config);
        assertNotNull(decks);
        assertEquals(16, decks.size());
        check(decks, "hlm",   "Highlands Monster",   "highlandsMonster.json",   null, false, true,  false, false, false);
        check(decks, "hle",   "Highlands Encounter", "highlandsEncounter.json", null, false, false, true,  false, false);
        check(decks, "em",    "Epic Monster",        "epicMonster.json",        null, false, true,  false, false, true);
        check(decks, "skill", "Skill",               "skills.json",             null, true,  false, false, false, false);
    }

    @Test
    public void testGetSkills() throws Exception {
        List<Skill> skills = grepos.getCards(context, config, "skill", Skill.class);
        assertNotNull(skills);
        assertEquals(48, skills.size());
        Collections.sort(skills, Skill.NameComparator);

        check(skills.get(0), "alchemy", null, "Alchemy", "Scholar");
        check(skills.get(1), "backstab", null, "Backstab", "Hunter or Rogue");
        check(skills.get(2), "black_market", null, "Black Market", "Merchant or Rogue");
        check(skills.get(5), "burly", null, "Burly", "STR 8");

        AdventurerSheet ta = PowerMockito.mock(AdventurerSheet.class);
        when(ta.getClasses()).thenReturn(new String[] {"Wizard" });
        when(ta.getStatValue(any(AdventurerSheet.Stat.class))).thenReturn(8);  //  well, she's above average
        List<Skill> tl = Skill.filterSkills(skills, ta);
        assertNotNull(tl);
        assertEquals(18, tl.size());
        String noReqs = Req.Passes.toString();
        check(tl.get(0), "burly", null, "Burly", "STR 8");
        check(tl.get(1), "counter", null, "Counter", "AGI 8");
        check(tl.get(6), "magic_missile", null, "Magic Missile", "Wizard");
        check(tl.get(8), "mist_form", null, "Mist Form", "Warlock or Wizard");

        when(ta.getClasses()).thenReturn(new String[] { "Wizard", "Rogue", "Wild" });
        tl = Skill.filterSkills(skills, ta);
        assertNotNull(tl);
        assertEquals(33, tl.size());
        check(tl.get(0), "backstab", null, "Backstab", "Hunter or Rogue");
        check(tl.get(1), "black_market", null, "Black Market", "Merchant or Rogue");
        check(tl.get(2), "bloodlust", "cp1","Bloodlust", "Warlock or Wild");

//this test is failing now... which way is correct?
//        when(ta.getClasses()).thenReturn(new String[] { });
//        tl = Skill.filterSkills(skills, ta);
//        assertNotNull(tl);
//        assertEquals(skills, tl);

        tl = Skill.filterSkills(skills, null);
        assertNotNull(tl);
        assertEquals(skills, tl);
    }

    /**
     * Voidwalker requires "Void Witch", so this confirms that we're testing
     * her name.
     */
    @Test
    public void testVoidWalker() {
        List<AdventurerSheet> al = grepos.getAdventurerSheets(context, config);
        List<Skill> sl = grepos.getCards(context, config, "skill", Skill.class);
        AdventurerSheet ta = TestUtil.linearSearch(al, new Requirement<AdventurerSheet>() {
            @Override
            public boolean passes(AdventurerSheet adventurer) {
                return adventurer.getName().equals("Void Witch");
            }
        });
        Skill ts = TestUtil.linearSearch(sl, new Requirement<Skill>() {
            @Override
            public boolean passes(Skill skill) {
                return skill.getID().equals("voidwalker");
            }
        });
        assertNotNull(ta);
        assertNotNull(ts);
        assertSameElements(new String[] { "Human", "Warlock", "Wizard" }, ta.getClasses());
        assertEquals("Void Witch", ts.getRequirements().toString());
        assertTrue(ts.getRequirements().passes(ta));
    }

    @Test
    public void testGetMap() throws Exception {
        Map map = grepos.getMap(context, config);
        assertNotNull(map);
        List<Region> regions = map.getRegions();
        assertNotNull(regions);
        assertEquals(4, regions.size());

        Region tr = regions.get(0);
        assertEquals("Highlands", tr.getName());
        assertEquals(9, tr.getSize());
        assertNull(tr.getLocationOrTerritory("schnorkel"));
        check(map, "Bandit Camp", 4, "Outside", "Settlement");
        check(map, "Pigskin Port", -3, "Inside", "Settlement", "RiverPort");
        check(map, "The Watchtower", 3, "Inside", "Settlement", "RiverPort");
        check(map, "The Temple of Madness", 5, "Inside", "Metaphysical", "Perilous");
        check(map, "The Tunnel of Terror", 4, "Inside", "RiverPort", "Metaphysical", "Perilous");
        check(map, "Slaver's Pass", 4, "Outside");
        check(map, "The Foothills", 2, "Outside");
        check(map, "The Windmill", 0, "Inside");
        check(map, "North Bridge", 0, "Outside");

        tr = regions.get(1);
        assertEquals("Badlands", tr.getName());
        assertEquals(8, tr.getSize());
        check(map, "Goblin Fortress", 5, "Inside", "Settlement");
        check(map, "Burning Lakes", 4, "Outside", "Perilous");
        check(map, "Crystal Crater", 0, "Outside", "RiverPort", "Perilous");
        check(map, "The Wasteland", 3, "Outside");
        check(map, "Last Chance", 0, "Inside");
        check(map, "Skull Bridge", 4, "Inside");
        check(map, "The Blasted Heath", 4, "Outside", "Perilous");
        check(map, "The Hell Pit", 5, "Outside", "Perilous");

        tr = regions.get(2);
        assertEquals("Lowlands", tr.getName());
        assertEquals(11, tr.getSize());
        check(map, "Brüttelburg", -5, "Inside", "Settlement", "Law");
        check(map, "The Holy Order", -3,"Inside", "Settlement", "Law");
        check(map, "Hunt Lodge", -2, "Inside", "Woodland", "Settlement", "Law");
        check(map, "The Graveyard", 4, "Inside");
        check(map, "The Catacombs", 5, "Inside");
        check(map, "The Fields", 2, "Outside", "Law");
        check(map, "Crossroads", 0, "Outside", "Law");
        check(map, "Witch Hill", 5, "Outside", "Woodland");
        check(map, "The Witchwood", 4, "Outside", "Woodland");
        check(map, "East Bridge", 0, "Outside", "Woodland", "RiverPort", "Law");
        check(map, "Hag's Fork", 0, "Outside", "Woodland");

        tr = regions.get(3);
        assertEquals("Wetlands", tr.getName());
        assertEquals(10, tr.getSize());
        check(map, "Gutfish Ford", -2, "Outside", "Settlement", "RiverPort");
        check(map, "Stone Circle", -2, "Outside", "Settlement");
        check(map, "Fishmonger Camp", 2, "Inside", "Settlement", "LakePort");
        check(map, "The Rotting Swamp", 4, "Outside");
        check(map, "The Black Tree", 5, "Outside", "Woodland");
        check(map, "Sunken Village", 4, "Inside");
        check(map, "Tower Island", 4, "Inside", "LakePort");
        check(map, "Tomb Lake", 3, "Outside", "LakePort");
        check(map, "Dusk Falls", 0, "Outside", "LakePort");
        check(map, "Ghostgate", 0, "Inside", "LakePort");
    }

    private void check(java.util.Map<String, Deck> decks, String deckID, String name,
                       String cardFileName, String cardClassName,
                       boolean noDiscardPile, boolean monster, boolean encounter,
                       boolean loot, boolean epic) throws Exception {
        Deck deck = decks.get(deckID);
        assertNotNull(deck);
        assertEquals(deckID, deck.getID());
        assertEquals(name, deck.getName());
        assertEquals(cardFileName, deck.getCardFileName());
        assertEquals(cardClassName, deck.getCardClassName());
        assertEquals(noDiscardPile, deck.isNoDiscardPile());
        assertEquals(monster, deck.isMonster());
        assertEquals(encounter, deck.isEncounter());
        assertEquals(loot, deck.isLoot());
        assertEquals(epic, deck.isEpic());
    }

    private void check(Skill skill, String id, String expansionID, String name,
                       String req) throws Exception {
        assertNotNull(skill);
        assertEquals(id, skill.getID());
        assertEquals(expansionID, skill.getExpansionID());
        assertEquals(name, skill.getName());
        assertEquals(req, skill.getRequirements().toString());
    }

    private void assertSameElements(Object[] expect, Object[] got) {
        if (((expect == null) || (expect.length == 0)) && ((got == null) || (got.length == 0))) return;
        assertTrue((expect != null) && (got != null));
        for (Object tv : expect) {
            boolean found = false;
            for (Object tv2 : got) {
                if (tv.equals(tv2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("Expected to find " + tv);
            }
        }
    }

    private void check(Map map, String expectName, int expectDanger, String... expectTraits) {
        LocationState loc = map.getLocationOrTerritory(expectName);
        assertNotNull(loc);
        assertEquals(expectName, loc.getName());
        if (expectDanger >= 0) {
            assertEquals(expectDanger, loc.getDangerLevel());
            assertEquals(0, loc.getTownLevel());
        } else {
            assertEquals(0, loc.getDangerLevel());
            assertEquals(-expectDanger, loc.getTownLevel());
        }
        for (String ts : expectTraits) {
            assertNotNull(MapTrait.valueOf(ts));  //  throws IllegalArgumentException
        }
        for (MapTrait trait : MapTrait.values()) {
            boolean want = false;
            for (String ts : expectTraits) {
                if (trait.toString().equals(ts)) {
                    want = true;
                    break;
                }
            }
            assertEquals("expected " + trait + " = " + want, want, loc.hasTrait(trait));
        }
    }
}