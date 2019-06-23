package com.kuhrusty.morbadscorepad;

import android.content.Context;

import com.kuhrusty.morbadscorepad.model.AdventurerSheet;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.Skill;
import com.kuhrusty.morbadscorepad.model.dao.CachingGameRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository2;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RequirementTest {
    @Test
    public void testAndAndOrOrOrAndAnd() throws Exception {
        Requirement<String> True = new Requirement<String>() {
            @Override
            public boolean passes(String s) {
                return true;
            }
        };
        Requirement<String> False = new Requirement<String>() {
            @Override
            public boolean passes(String s) {
                return false;
            }
        };
        String obj = "foo";  //  doesn't matter
        assertTrue(True.passes(obj));
        assertFalse(False.passes(obj));
        assertTrue(new Requirement.Or(True).passes(obj));
        assertFalse(new Requirement.Or(False).passes(obj));
        assertTrue(new Requirement.Or(True, True).passes(obj));
        assertTrue(new Requirement.Or(True, False).passes(obj));
        assertTrue(new Requirement.Or(False, True).passes(obj));
        assertTrue(new Requirement.Or(False, False, False, True).passes(obj));
        assertTrue(new Requirement.Or(False, False, False, True, False).passes(obj));
        assertFalse(new Requirement.Or(False, False, False, False).passes(obj));

        assertTrue(new Requirement.And(True).passes(obj));
        assertFalse(new Requirement.And(False).passes(obj));
        assertTrue(new Requirement.And(True, True).passes(obj));
        assertFalse(new Requirement.And(True, False).passes(obj));
        assertFalse(new Requirement.And(False, True).passes(obj));
        assertFalse(new Requirement.And(False, False, False, True).passes(obj));
        assertFalse(new Requirement.And(False, False, False, True, False).passes(obj));
        assertFalse(new Requirement.And(False, False, False, False).passes(obj));
        assertTrue(new Requirement.And(True, True, True, True).passes(obj));
    }

    @Test
    public void testParse() {
        assertEquals("MRL 8", Req.parse("MRL 8").toString());
        assertEquals("MRL 8", Req.parse(" MRL 8").toString());
        assertEquals("MRL 8", Req.parse("MRL 8 ").toString());
        assertEquals("MRL 8", Req.parse("  MRL   8  ").toString());
        assertEquals("STR <9", Req.parse("  STR   <9  ").toString());
        assertEquals("STR <9", Req.parse("  STR   <    9  ").toString());
        assertEquals("MRL 8 or STR 7", Req.parse("MRL 8 or STR 7").toString());
        assertEquals("MRL 8 or STR 7", Req.parse("MRL 8 OR STR 7").toString());
        assertEquals("MRL 8 or STR 7", Req.parse("    MRL      8 or  STR 7 ").toString());
        assertEquals("MRL 8 or STR <7", Req.parse("    MRL      8 or  STR <7 ").toString());
        assertEquals("MRL 8 or STR <7", Req.parse("    MRL      8 or  STR <  7 ").toString());
        assertEquals("MRL <5 or STR 6", Req.parse("    MRL <     5 or  STR  6 ").toString());
        assertEquals("MRL <5 or STR <6", Req.parse("    MRL <     5 or  STR  <6 ").toString());
        assertEquals("MRL <5 or STR <6", Req.parse("    MRL <     5 or  STR  < 6 ").toString());

        assertEquals("Wizard", Req.parse("Wizard").toString());
        assertEquals("Wizard", Req.parse("  Wizard  ").toString());
        assertEquals("Wild or Wizard", Req.parse("Wild or Wizard").toString());
        assertEquals("Wild or Wizard", Req.parse("     Wild or Wizard ").toString());
        assertEquals("Rogue, Wild, or Wizard", Req.parse(" Rogue,    Wild or Wizard ").toString());
        assertEquals("Rogue, Wild, or Wizard", Req.parse(" Rogue,    Wild, or Wizard ").toString());
        assertEquals("Rogue, Wild, or Wizard", Req.parse(" Rogue,    Wild, Wizard ").toString());
        assertEquals("Rogue or Void Witch", Req.parse(" Rogue,    Void Witch ").toString());
        assertEquals("Rogue or Void Witch", Req.parse(" Rogue or  Void Witch ").toString());
        assertEquals("AGI 7 and PER 7", Req.parse("AGI 7 AND PER 7").toString());
    }

    /**
     * This is currently the only skill with an "and" condition.
     */
    @Test
    public void testLurkerRequirement() {
        Context context = TestUtil.mockContext();
        GameRepository grepos = new CachingGameRepository(new JSONGameRepository2());//JSONGameRepository2();
        GameConfiguration config = new GameConfiguration(context, "HandOfDoom", grepos, "cp1", "cp2");
        List<AdventurerSheet> al = grepos.getAdventurerSheets(context, config);
        //  no one has an AGI below 7
        //Adventurer firstBad = Util.find(al, new Adventurer.NameRequirement(...));
        AdventurerSheet secondBad = Util.find(al, new AdventurerSheet.NameRequirement("Dishonored Knight"));
        //Adventurer bothBad = Util.find(al, new Adventurer.NameRequirement("..."));
        AdventurerSheet bothGood = Util.find(al, new AdventurerSheet.NameRequirement("Charlatan Magician"));
        List<Skill> skills = grepos.getCards(context, config, "skill", Skill.class);
        Skill.NameRequirement lurker = new Skill.NameRequirement("Lurker");
        assertNotNull(Util.find(skills, lurker));
        List<Skill> ts = Skill.filterSkills(skills, secondBad);
        assertNull(Util.find(ts, lurker));
        ts = Skill.filterSkills(skills, bothGood);
        assertNotNull(Util.find(ts, lurker));
    }

    /**
     * Dilettante gives you access to skills whose requirements you might not
     * otherwise meet.
     */
    @Test
    public void testDilettanteRequirement() {
        Context context = TestUtil.mockContext();
        GameRepository grepos = new CachingGameRepository(new JSONGameRepository2());
        GameConfiguration config = new GameConfiguration(context, "HandOfDoom", grepos, "all");

        //  If this line fails, you've added an expansion, and haven't confirmed
        //  that Skill.DilettanteRequirement works correctly for any new skills
        //  included in that expansion.
        assertEquals(7, config.getExpansionCount());

        List<Skill> skills = grepos.getCards(context, config, "skill", Skill.class);

        //  Similarly, if this line fails, then the new expansion you've added
        //  contains new skills, and you need to confirm that
        //  Skill.DilettanteRequirement gives the right answer for them.
        assertEquals(72, skills.size());

        int pass = 0;
        for (int ii = 0; ii < skills.size(); ++ii) {
            if (Skill.DilettanteRequirement.passes(skills.get(ii))) {
                ++pass;
            }
        }
        assertEquals(28, pass);
    }
}