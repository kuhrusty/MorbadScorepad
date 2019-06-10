package com.kuhrusty.morbadscorepad.model;

import android.content.Context;

import com.kuhrusty.morbadscorepad.TestUtil;
import com.kuhrusty.morbadscorepad.Util;
import com.kuhrusty.morbadscorepad.model.dao.CachingGameRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository2;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * See also JSONGameRepositoryTest.testGetSkills() and RequirementTest.
 */
public class SkillTest {
    @Test
    public void testXPLimit() throws Exception {
        Context context = TestUtil.mockContext();
        GameRepository grepos = new CachingGameRepository(new JSONGameRepository2());
        GameConfiguration config = new GameConfiguration(context, "HandOfDoom", grepos, "cp1", "cp2");
        List<Skill> skills = grepos.getCards(context, config, "skill", Skill.class);
        assertEquals(63, skills.size());
        Collections.sort(skills, Skill.NameComparator);
        assertEquals("Alchemy", skills.get(0).getName());
        assertEquals(8, skills.get(0).getXP());

        List<AdventurerSheet> al = grepos.getAdventurerSheets(context, config);
        AdventurerSheet witchSmeller = Util.find(al, new AdventurerSheet.NameRequirement("Witch Smeller"));
        List<Skill> ts = Skill.filterSkills(skills, witchSmeller, 0);
        assertEquals(25, ts.size());
        assertEquals("Backstab", ts.get(0).getName());
        assertEquals(10, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, witchSmeller, 21);
        assertEquals(25, ts.size());
        assertEquals("Backstab", ts.get(0).getName());
        assertEquals(10, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, witchSmeller, 12);
        assertEquals(23, ts.size());
        assertEquals("Backstab", ts.get(0).getName());
        assertEquals(10, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, witchSmeller, 10);
        assertEquals(23, ts.size());
        assertEquals("Backstab", ts.get(0).getName());
        assertEquals(10, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, witchSmeller, 8);
        assertEquals(16, ts.size());
        assertEquals("Dauntless", ts.get(0).getName());
        assertEquals(6, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, witchSmeller, 6);
        assertEquals(7, ts.size());
        assertEquals("Dauntless", ts.get(0).getName());
        assertEquals(6, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, witchSmeller, 4);
        assertEquals(0, ts.size());

        ts = Skill.filterSkills(skills, null, 0);
        assertEquals(63, ts.size());
        assertEquals("Alchemy", ts.get(0).getName());
        assertEquals(8, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, null, 21);
        assertEquals(63, ts.size());

        ts = Skill.filterSkills(skills, null, 12);
        assertEquals(59, ts.size());
        assertEquals("Alchemy", ts.get(0).getName());
        assertEquals(8, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, null, 10);
        assertEquals(59, ts.size());
        assertEquals("Alchemy", ts.get(0).getName());
        assertEquals(8, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, null, 8);
        assertEquals(47, ts.size());
        assertEquals("Alchemy", ts.get(0).getName());
        assertEquals(8, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, null, 6);
        assertEquals(19, ts.size());
        assertEquals("Burly", ts.get(0).getName());
        assertEquals(6, ts.get(0).getXP());

        ts = Skill.filterSkills(skills, null, 4);
        assertEquals(0, ts.size());
    }
}
