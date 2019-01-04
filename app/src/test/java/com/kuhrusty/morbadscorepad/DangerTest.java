package com.kuhrusty.morbadscorepad;

import android.content.Context;

import com.kuhrusty.morbadscorepad.model.Danger;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.DeckState;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.dao.CachingGameRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository2;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DangerTest {

    private Context context;
    private GameRepository grepos;
    private GameConfiguration config;
    List<Danger> dangerCards;
    Deck<Danger> dangerDeck;

    @Before
    public void before() {
        context = TestUtil.mockContext();
        grepos = new CachingGameRepository(new JSONGameRepository2());
        config = new GameConfiguration(context, "HandOfDoom", grepos, "cp1", "cp2");

        dangerCards = grepos.getCards(context, config, "danger", Danger.class);
        assertEquals(36, dangerCards.size());

        dangerDeck = grepos.getDeck(context, config, "danger", Danger.class);
        assertEquals(36, dangerDeck.getCards().size());
    }

    @Test
    public void testCardStuff() {
        DeckState<Danger> ds = new DeckState<>(Danger.class, dangerDeck);
        ds.setRandom(new Random(666));  //  so we get... repeatable results
        ds.shuffle();
        check(ds, 36, false, false, "witchwood", null);

        Danger danger = ds.draw();
        check(danger, "witchwood");
        check(ds, 35, true, false, "witch_hill", "witchwood");

        danger = ds.draw();
        check(danger, "witch_hill");
        check(ds, 34, true, false, "foothills", "witch_hill");

        assertTrue(ds.undo());
        check(ds, 35, true, true, "witch_hill", "witchwood");

        assertTrue(ds.undo());
        //  now we're back at the top of the undo stack
        check(ds, 36, false, true, "witchwood", null);

        assertFalse(ds.undo());
        check(ds, 36, false, true, "witchwood", null);

        assertTrue(ds.redo());
        check(ds, 35, true, true, "witch_hill", "witchwood");

        assertTrue(ds.redo());
        check(ds, 34, true, false, "foothills", "witch_hill");

        assertFalse(ds.redo());
        check(ds, 34, true, false, "foothills", "witch_hill");

        for (int ti = 34; ti >= 0; --ti) {
            ds.draw();
        }
        assertEquals(0, ds.cardsInDrawPile());
        assertTrue(ds.canUndo());
        assertFalse(ds.canRedo());
        assertNull(ds.draw());
        assertNull(ds.draw());
        assertTrue(ds.canUndo());
        assertFalse(ds.canRedo());
        assertTrue(ds.undo());
        check(ds, 1, true, true, "goblin_fortress", "wasteland");

        ds.shuffle();
        check(ds, 36, true, false, "pigskin_port", null);
        ds.draw();
        ds.draw();
        ds.draw();
        check(ds, 33, true, false, "temple_of_madness", "gutfish_ford");
        ds.shuffleDrawPile();
        //  the draw pile size should be the same, and the same card should be
        //  the top discard, but a different card should now be at the top of
        //  the draw pile.
        check(ds, 33, true, false, "wasteland", "gutfish_ford");
        assertTrue(ds.undo());
        //  we should be back where we were before shuffleDrawPile(), except now
        //  we can redo
        check(ds, 33, true, true, "temple_of_madness", "gutfish_ford");
        assertTrue(ds.redo());
        check(ds, 33, true, false, "wasteland", "gutfish_ford");
        assertTrue(ds.undo());
        assertTrue(ds.undo());
        check(ds, 34, true, true, "gutfish_ford", "hunt_lodge");
        //  Make sure we can undo across shuffles
        assertTrue(ds.undo());
        assertTrue(ds.undo());
        check(ds, 36, true, true, "pigskin_port", null);
        assertTrue(ds.undo());
        check(ds, 1, true, true, "goblin_fortress", "wasteland");
        assertTrue(ds.undo());
        check(ds, 2, true, true, "wasteland", "rotting_swamp");
        assertTrue(ds.redo());
        assertTrue(ds.redo());
        check(ds, 36, true, true, "pigskin_port", null);

        //  Now let's try running with no log.
        ds.disableLog();
        ds.shuffle();
        check(ds, 36, false, false, "tunnel_of_terror", null);
        ds.draw();
        check(ds, 35, false, false, "gutfish_ford", "tunnel_of_terror");
        ds.draw();
        check(ds, 34, false, false, "rotting_swamp", "gutfish_ford");
        ds.shuffleDrawPile();
        check(ds, 34, false, false, "graveyard", "gutfish_ford");
        assertFalse(ds.undo());
        check(ds, 34, false, false, "graveyard", "gutfish_ford");
        assertFalse(ds.redo());
        check(ds, 34, false, false, "graveyard", "gutfish_ford");

        //  If we turn the log back on, is the deck still functional, and can we get back to our last shuffle?
        ds.enableLog();
        check(ds, 34, true, false, "graveyard", "gutfish_ford");
        assertTrue(ds.undo());
        check(ds, 35, true, true, "gutfish_ford", "tunnel_of_terror");
        assertTrue(ds.undo());
        check(ds, 36, false, true, "tunnel_of_terror", null);
        assertTrue(ds.redo());
        assertTrue(ds.redo());
        check(ds, 34, true, false, "graveyard", "gutfish_ford");
        assertFalse(ds.redo());
        check(ds, 34, true, false, "graveyard", "gutfish_ford");

        //  Make sure enableLog() works when there's no draw pile
        ds.disableLog();
        while(ds.draw() != null) ;  //  whee
        check(ds, 0, false, false, null, "sunken_village");
        ds.enableLog();
        check(ds, 0, true, false, null, "sunken_village");
        assertTrue(ds.undo());
        check(ds, 1, true, true, "sunken_village", "black_tree");

        //  Make sure enableLog() works when there's no discard pile
        ds.disableLog();
        ds.shuffle();
        check(ds, 36, false, false, "gutfish_ford", null);
        ds.enableLog();
        check(ds, 36, false, false, "gutfish_ford", null);
        ds.draw();
        check(ds, 35, true, false, "slavers_pass", "gutfish_ford");

        danger = ds.findByID("foophills");
        assertNull(danger);
        danger = ds.findByID("foothills");
        assertNotNull(danger);
        assertFalse(danger.isReshuffle());
        danger = ds.findByID("none6");
        assertNotNull(danger);
        assertTrue(danger.isReshuffle());
    }

    /**
     * Looking at the code just now, I didn't see where we trim the undo log if
     * someone does an undo() and then a shuffle; it looks like we just add the
     * shuffle to the end of the log and point at it without whacking all the
     * undone elements.
     */
    @Test
    public void testShuffleAfterUndo() {
        DeckState<Danger> ds = new DeckState<>(Danger.class, dangerDeck);
        ds.setRandom(new Random(666));  //  so we get... repeatable results
        ds.shuffle();
        check(ds, 36, false, false, "witchwood", null);

        ds.draw();
        ds.draw();
        ds.draw();
        check(ds, 33, true, false, "none1", "foothills");
        ds.undo();
        ds.undo();
        assertEquals(4, ds.getLogSize());
        check(ds, 35, true, true, "witch_hill", "witchwood");

        ds.shuffle();
        check(ds, 36, true, false, "pigskin_port", null);
        //  we should've lost those two undos, then added the shuffle
        assertEquals(3, ds.getLogSize());
        ds.undo();
        check(ds, 35, true, true, "witch_hill", "witchwood");
    }

    private void check(Danger card, String expectID) {
        assertEquals(expectID, card.getID());
    }

    private void check(DeckState<Danger> deck, int expectDrawSize,
                       boolean expectUndo, boolean expectRedo, String expectTopID,
                       String expectTopDiscardID) {
        assertEquals(expectDrawSize, deck.cardsInDrawPile());
        assertEquals(expectUndo, deck.canUndo());
        assertEquals(expectRedo, deck.canRedo());

        Danger td = deck.peek();
        if (expectTopID != null) {
            assertNotNull(td);
            assertEquals(expectTopID, td.getID());
        } else {
            assertNull(td);
        }

        td = deck.getTopDiscard();
        if (expectTopDiscardID != null) {
            assertNotNull(td);
            assertEquals(expectTopDiscardID, td.getID());
        } else {
            assertNull(td);
        }
    }
}