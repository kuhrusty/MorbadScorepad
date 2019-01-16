package com.kuhrusty.morbadscorepad;

import android.content.Context;

import com.google.gson.Gson;
import com.kuhrusty.mockparcel.MockParcel;
import com.kuhrusty.morbadscorepad.model.BadDataException;
import com.kuhrusty.morbadscorepad.model.Danger;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.DeckState;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.dao.CachingGameRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.dao.RepositoryFactory;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.kuhrusty.morbadscorepad.TestUtil.compare;
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

    @Rule
    public ExpectedException expectThrown = ExpectedException.none();

    @Before
    public void before() {
        Util.setContextAndConfig(null, null);
        context = TestUtil.mockContext();
        grepos = new CachingGameRepository(new JSONGameRepository2());
        RepositoryFactory.setGameRepository(grepos);
        config = new GameConfiguration(context, "HandOfDoom", grepos, "cp1", "cp2");

        dangerCards = grepos.getCards(context, config, "danger", Danger.class);
        assertEquals(36, dangerCards.size());

        dangerDeck = grepos.getDeck(context, config, "danger", Danger.class);
        assertEquals(36, dangerDeck.getCards().size());
    }

    @Test
    public void testCardStuff() throws Exception {
        DeckState<Danger> ds = new DeckState<>(Danger.class, dangerDeck);
        DeckState<Danger> ds2;
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
        //  As long as we've got a reasonably complex DeckState, confirm that
        //  it survives Parcel.
        ds2 = checkJSONAndParcel(ds);

        //  well... the seed is not being preserved across parceling.
        ds2.setRandom(TestUtil.cloneRandom(ds.getRandom()));

        ds.shuffle();
        check(ds, 36, true, false, "pigskin_port", null);
        ds2.shuffle();
        check(ds2, 36, true, false, "pigskin_port", null);
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

        //  Horrible mix of tests here, but let's Parcel this thing again, but
        //  this time without a GameRepository in the RepositoryFactory.
        RepositoryFactory.setGameRepository(null);
        checkJSON(ds);
        RepositoryFactory.setGameRepository(null);
        checkParcel(ds);
        RepositoryFactory.setGameRepository(grepos);

        //  Well, this is a deck state with a reasonably complex undo history...
        //  let's see how the JSON looks.
        Gson gson = JSONGameRepository.newGsonBuilder().setPrettyPrinting().create();
        String outfile = "build/tmp/DangerDeckActivity.deck.json";
        FileWriter fw = new FileWriter(outfile);
        gson.toJson(ds, fw);
        fw.close();
        compare("DangerTest.2.json", outfile, true, false);

        Danger[] savedOrder = ds.getOrder();  //  see comment below for reason

        assertEquals(41, ds.getLogSize());
        //  we have 3 shuffles; confirm that this doesn't delete anything:
        ds.setShuffleLogLimit(3);
        assertEquals(41, ds.getLogSize());
        //  now undo past shuffle #2, so that we can't delete that third shuffle
        //  without losing our log position
        ds.undo();
        ds.setShuffleLogLimit(2);
        assertEquals(41, ds.getLogSize());
        //  now redo past shuffle #2, so that the 3rd shuffle back can be trimmed
        ds.redo();
        assertTrue(ds.canUndo());
        ds.setShuffleLogLimit(2);
        assertEquals(5, ds.getLogSize());
        assertFalse(ds.canUndo());
        ds.redo();
        assertTrue(ds.canUndo());
        ds.setShuffleLogLimit(1);  //  log pos too far back again
        assertEquals(5, ds.getLogSize());
        while (ds.redo()) ; //  wheee
        assertTrue(ds.canUndo());
        ds.setShuffleLogLimit(1);
        assertEquals(1, ds.getLogSize());
        assertFalse(ds.canUndo());
        ds.setShuffleLogLimit(-666);
        assertEquals(0, ds.getLogSize());

        //  Now let's try running with no log.
        ds.disableLog();
        //  so, I added the trimLog() stuff in the middle of this test, which
        //  screwed up the rest of this test because shuffle() gives different
        //  results based on the *existing* order of the deck... so put the
        //  order back how it was before I added the trimLog() tests.
        ds.setOrder(savedOrder);
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

        //  and Parcel with no log
        ds2 = checkJSONAndParcel(ds);

        //  actually, I want to take a look at the JSON.
        fw = new FileWriter(outfile);
        gson.toJson(ds, fw);
        fw.close();
        compare("DangerTest.3.json", outfile, true, false);

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

        //  same thing should happen with a draw after an undo.
        ds.draw();
        assertEquals(3, ds.getLogSize());  //  lose the shuffle, add the draw
        ds.undo();
        check(ds, 35, true, true, "witch_hill", "witchwood");
    }

    /**
     * This tests a case I'm seeing where we restore a deck from file, undo, and
     * explode.
     */
    @Test
    public void testUndoAfterRead() throws Exception {
        DeckState<Danger> ds = new DeckState<>(Danger.class, dangerDeck);
        ds.setRandom(new Random(666));  //  so we get... repeatable results
        ds.shuffle();
        check(ds, 36, false, false, "witchwood", null);

        ds.draw();
        ds.draw();
        ds.draw();
        check(ds, 33, true, false, "none1", "foothills");

        //  Save our deck state to file
        Gson gson = JSONGameRepository.newGsonBuilder().setPrettyPrinting().create();
        String outfile = "build/tmp/DangerDeckActivity.deck.json";
        FileWriter fw = new FileWriter(outfile);
        gson.toJson(ds, fw);
        fw.close();
        compare("DangerTest.1.json", outfile, true, false);

        //  Load our deck state from file
        try {
            Util.setContextAndConfig(context, config);
            ds = (DeckState<Danger>) (gson.fromJson(new FileReader(outfile), DeckState.class));
        } finally {
            Util.setContextAndConfig(null, null);
        }

        check(ds, 33, true, false, "none1", "foothills");
        ds.undo();
        ds.undo();
        check(ds, 35, true, true, "witch_hill", "witchwood");
        ds.undo();
        check(ds, 36, false, true, "witchwood", null);
    }

    /**
     * DeckStateActivity writes its DeckState to file when shutting down; it
     * needs to handle the case where a card ID has changed or been removed
     * since we last ran.
     */
    @Test
    public void testBadDangerCardID() throws Exception {
        String json = TestUtil.snort("DangerTest.badID.json", true, true, false);
        Gson gson = JSONGameRepository.newGsonBuilder().create();

        Util.setContextAndConfig(context, config);
        expectThrown.expect(BadDataException.class);
        expectThrown.expectMessage("card ID \"fnub\" isn't in the deck; giving up");
        gson.fromJson(json, DeckState.class);
    }

    /**
     * Similarly, if a new Danger card has been <i>added</i> to the deck since
     * we saved our file, we need to handle that.
     */
    @Test
    public void testMissingDangerCard() throws Exception {
        String json = TestUtil.snort("DangerTest.missingCard.json", true, true, false);
        Gson gson = JSONGameRepository.newGsonBuilder().create();

        Util.setContextAndConfig(context, config);
        expectThrown.expect(BadDataException.class);
        expectThrown.expectMessage("read 35 card IDs, but 36 cards in deck; giving up");
        gson.fromJson(json, DeckState.class);
    }

    /**
     * This is for developers: confirm that we throw a better exception than
     * NullPointerException when no context or GameConfiguration has been set
     * before trying to deserialize, a mistake I keep making.
     */
    @Test
    public void testMissingContext() throws Exception {
        String json = TestUtil.snort("DangerTest.1.json", true, true, false);
        Gson gson = JSONGameRepository.newGsonBuilder().create();

        expectThrown.expect(RuntimeException.class);
        expectThrown.expectMessage("null context or config; call " +
                "Util.setContextAndConfig() before deserializing DeckState");
        gson.fromJson(json, DeckState.class);
    }

    /**
     * Do we correctly save/load a deck which hasn't had shuffle() called yet?
     */
    @Test
    public void testNoShuffle() throws Exception {
        DeckState<Danger> ds = new DeckState<>(Danger.class, dangerDeck);
        assertEquals(0, ds.getLogSize());
        assertNull(ds.getOrder());
        assertNull(ds.getTopDiscard());
        assertNull(ds.peek());

        //  well... this is not exactly the behavior I was hoping for, but it's
        //  not a case I care enough about to go fix.  Just don't save a
        //  DeckState you haven't done anything with yet.
        expectThrown.expect(BadDataException.class);
        expectThrown.expectMessage("read 0 card IDs, but 36 cards in deck; giving up");
        DeckState<Danger> ds2 = checkJSONAndParcel(ds);
        //assertEquals(0, ds2.getLogSize());
        //assertNull(ds2.getOrder());
        //assertNull(ds2.getTopDiscard());
        //assertNull(ds2.peek());
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

    private DeckState<Danger> checkJSON(DeckState<Danger> ds) throws IOException {
        Util.setContextAndConfig(context, config);
        DeckState<Danger> rv = TestUtil.json(ds, DeckState.class);
        Util.setContextAndConfig(null, null);
        check(ds, rv);
        return rv;
    }

    private DeckState<Danger> checkParcel(DeckState<Danger> ds) {
        Util.setContextAndConfig(context, config);
        DeckState<Danger> rv = MockParcel.parcel(ds, DeckState.CREATOR);
        Util.setContextAndConfig(null, null);
        check(ds, rv);
        return rv;
    }

    private DeckState<Danger> checkJSONAndParcel(DeckState<Danger> ds) throws IOException {
        checkJSON(ds);
        return checkParcel(ds);
    }

    /**
     * Confirms that the two DeckStates are not the same instance, but that they
     * have the same top-of-deck and top-of-discard, and maybe other stuff.
     */
    private void check(DeckState<Danger> expect, DeckState<Danger> got) {
        assertTrue(expect != got);
        String expectTopID = (expect.peek() != null) ? expect.peek().getID() : null;
        String expectTopDiscardID = (expect.getTopDiscard() != null) ? expect.getTopDiscard().getID() : null;
        check(got, expect.cardsInDrawPile(), expect.canUndo(), expect.canRedo(), expectTopID, expectTopDiscardID);
    }
}
