package com.kuhrusty.morbadscorepad.model;

import android.content.Context;
import android.os.Parcel;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.kuhrusty.parcelorgson.CreatorSD;
import com.kuhrusty.parcelorgson.GsonWrapper;
import com.kuhrusty.parcelorgson.ParcelOrGson;
import com.kuhrusty.parcelorgson.ParcelWrapper;
import com.kuhrusty.parcelorgson.Parcelable;
import android.util.Log;

import com.kuhrusty.morbadscorepad.NotDoneException;
import com.kuhrusty.morbadscorepad.Util;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.dao.RepositoryFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This keeps track of the order of a deck of cards and its discard pile, and an
 * optional undo log of changes to that order.
 */
public class DeckState<T extends Card> implements Parcelable {
    private static final String LOGBIT = "DeckState";

    private static final int NO_LOG = -1;
    private static final int DISABLE_LOG = -2;

    private static class LogEntry<T> {
        //  which card in the current order is at the top of the stack; -1 if
        //  the draw pile is empty.
        final int tos;
        LogEntry(int tos) {
            this.tos = tos;
        }
        boolean isShuffle() { return false; }
    }
    //  We also use this for storing cards being added to/removed from the deck.
    private static class ShuffleEntry<T> extends LogEntry<T> {
        final T[] order;
        //  how many cards have been removed; stored at high end of "order"
        final int removed;
        ShuffleEntry(int tos, int removed, T[] order) {
            super(tos);
            this.order = order;
            this.removed = removed;
        }
        @Override
        boolean isShuffle() { return true; }
    }

    /**
     * This is only public so that its CREATOR is visible to Parcel.  (or maybe
     * just MockParcel in unit tests.)  We have one LogEntry per card draw, but
     * for serialization, we can pack those down to one ShuffleEntry and a count
     * of the subsequent draws.
     */
    public static class PackedLogEntry implements android.os.Parcelable {
        int draws;
        int tos;
        int removed;
        int[] order;

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(draws);
            parcel.writeInt(tos);
            parcel.writeInt(removed);
            parcel.writeIntArray(order);
        }
        public static final Creator<PackedLogEntry> CREATOR = new Creator<PackedLogEntry>() {
            public PackedLogEntry createFromParcel(Parcel in) {
                PackedLogEntry rv = new PackedLogEntry();
                rv.draws = in.readInt();
                rv.tos = in.readInt();
                rv.removed = in.readInt();
                rv.order = in.createIntArray();
                return rv;
            }
            public PackedLogEntry[] newArray(int size) {
                return new PackedLogEntry[size];
            }
        };
        @Override
        public int describeContents() {
            return 0;
        }
    }
    //  to support any fmt = 1 files sitting around
    public static class PackedLogEntryV1 extends PackedLogEntry {
        public static final Creator<PackedLogEntryV1> CREATOR = new Creator<PackedLogEntryV1>() {
            public PackedLogEntryV1 createFromParcel(Parcel in) {
                PackedLogEntryV1 rv = new PackedLogEntryV1();
                rv.draws = in.readInt();
                rv.tos = in.readInt();
                rv.removed = 0;
                rv.order = in.createIntArray();
                return rv;
            }
            public PackedLogEntryV1[] newArray(int size) {
                return new PackedLogEntryV1[size];
            }
        };
    }

    private Class<T> tclass;
    private Deck<T> deck;
    //  later entries are appended to the log.
    private List<LogEntry<T>> log;
    private int logpos = NO_LOG;
    private int shuffleLogLimit = 5;  //  just a guess at what would be good
    private T[] order;  //  reference to order from last ShuffleEntry
    private int tos;  //  copied from last LogEntry
    private int removed;  //  copied from last ShuffleEntry
    //  So, in the variables above, "order" is the array of cards; tos points
    //  to the top-of-stack (the card at the top of the draw deck); and
    //  "removed" tells us how many entries have been removed from the deck
    //  (those are stored at the high end of the array).  So, in a deck with 5
    //  cards, it might look like this after shuffling:
    //    4  card3
    //    3  card1
    //    2  card2
    //    1  card5
    //    0  card4
    //    (tos = 4, removed = 0)
    //  When a card is drawn, that's card3; tos = 3 tells us that card1 is at
    //  the top of the draw pile.  If we remove card2 and card5, we're going to
    //  wind up with this:
    //    4  card2
    //    3  card5
    //    2  card3
    //    1  card1
    //    0  card4
    //    (tos = 1, removed = 2)
    //  That tells us that card1 is still at the top of the stack; card2 and
    //  card5 have been removed from the deck, because removed == 2 and they're
    //  the top 2 cards.

    private Random rand;

    //public DeckState(Class<T> tclass) {
    //    this.tclass = tclass;
    //}
    public DeckState(Class<T> tclass, Deck<T> deck) {
        this.tclass = tclass;
        this.deck = deck;
    }

    public DeckState(ParcelOrGson in) {
        Context context = Util.getContext();
        GameConfiguration config = Util.getConfig();
        if ((context == null) || (config == null)) {
            throw new NullPointerException("null context or config; call " +
                    "Util.setContextAndConfig() before deserializing DeckState");
        }
        GameRepository grepos = RepositoryFactory.getGameRepository();
        String deckID = in.readString("id");
        int fmt = in.readInt("fmt", 1);  //  see comment in writeTo()
        if (fmt > 2) throw new BadDataException("I can't handle fmt " + fmt);
        String cn = in.readString("class");
        try {
            tclass = (Class<T>) getClass().getClassLoader().loadClass(cn);
        } catch (ClassNotFoundException cnfe) {
            throw new BadDataException("bad class name?", cnfe);
        }
        deck = grepos.getDeck(context, config, deckID, tclass);
        if (deck.getCards() == null) {
            //  really great API you have here.  getCards() theoretically has
            //  the side effect of setting the list of Cards on the Deck, which
            //  is incredibly dumb.  If I were getting paid for this, I'd fire
            //  myself.
            grepos.getCards(context, config, deckID, tclass);
        }
        List<T> cards = deck.getCards();
        if (cards == null) {
//there's really no other possible explanation.
throw new RuntimeException("RUSTY IS A GODDAMN BOZO");
        }
        java.util.Map<String, T> idMap = new HashMap<>();
        for (int ii = cards.size() - 1; ii >= 0; --ii) {
            idMap.put(cards.get(ii).getID(), cards.get(ii));
        }
        tos = in.readInt("tos", -1);
        removed = 0;
        if (fmt > 1) {
            removed = in.readInt("removed", 0);
        }
        List<String> lut = in.readStrings("ids");
        if (lut.size() != cards.size()) {
            throw new BadDataException("read " + lut.size() + " card IDs, but " +
                    cards.size() + " cards in deck; giving up");
        }
        order = newArray(lut.size());
        for (int ii = lut.size() - 1; ii >= 0; --ii) {
            order[ii] = idMap.get(lut.get(ii));
            if (order[ii] == null) {
                throw new BadDataException("card ID \"" + lut.get(ii) +
                        "\" isn't in the deck; giving up");
            }
        }
        shuffleLogLimit = in.readInt("logLimit", shuffleLogLimit);
        logpos = in.readInt("logpos", logpos);
        if (logpos >= 0) {
            List<? extends PackedLogEntry> ples = in.readList("log",
                    fmt == 1 ? PackedLogEntryV1.class : PackedLogEntry.class);
            log = new ArrayList<>();
            //  The latest entry is at the start of the list, so let's walk
            //  backward through the packed entries.
            for (int ii = ples.size() - 1; ii >= 0; --ii) {
                PackedLogEntry ple = ples.get(ii);
                T[] eorder = newArray(order.length);
                for (int jj = ple.order.length - 1; jj >= 0; --jj) {
                    //  order[] has our Card instances; ple.order[] has each
                    //  element's index into that array.
                    eorder[jj] = order[ple.order[jj]];
                }
                int ttos = ple.tos;
                log.add(new ShuffleEntry<>(ttos, ple.removed, eorder));
                for (int draw = ple.draws; draw > 0; --draw) {
                    log.add(new LogEntry<T>(--ttos));
                }
            }
        }
    }
    @Override
    public void writeTo(ParcelOrGson dest) {
        dest.writeString("id", deck.getID());
        //  put some format version number in case we change the structure
        //  later, and have saved JSON files hanging around in the old format.
        //  - fmt 2 added the "removed" element.
        dest.writeInt("fmt", 2);
        //  We don't actually care about the card class here; we're storing
        //  nothing about the card other than its ID.
        dest.writeString("class", tclass.getCanonicalName());
        dest.writeInt("tos", tos);
        dest.writeInt("removed", removed);
        List<String> ids;
        java.util.Map<String, Integer> idMap = (log != null) ?
                new java.util.HashMap<String, Integer>() : null;
        if (order != null) {
            ids = new ArrayList<String>(order.length);
            for (int ii = 0; ii < order.length; ++ii) {
                ids.add(order[ii].getID());
                if (idMap != null) idMap.put(order[ii].getID(), Integer.valueOf(ii));
            }
        } else {
            //  This is going to croak the read; see DangerTest.testNoShuffle().
            ids = Collections.emptyList();
        }
        dest.writeStrings("ids", ids);
        dest.writeInt("logLimit", shuffleLogLimit);
        dest.writeInt("logpos", logpos);
        if (logpos >= 0) {
            List<PackedLogEntry> ples = new ArrayList<PackedLogEntry>(shuffleLogLimit);
            PackedLogEntry ple = null;
            for (int ii = log.size() - 1; ii >= 0; --ii) {
                if (ple == null) ple = new PackedLogEntry();
                //  we very much hope that we get a ShuffleEntry first;
                //  otherwise this logic is probably broken.
                if (log.get(ii).isShuffle()) {
                    ShuffleEntry<T> shuffle = (ShuffleEntry<T>)(log.get(ii));
                    ple.tos = shuffle.tos;
                    ple.removed = shuffle.removed;
                    ple.order = new int[idMap.size()];
                    for (int jj = 0; jj < shuffle.order.length; ++jj) {
                        //  shuffle.order[] has our Card instances; idMap maps
                        //  those card IDs to their index in the "ids" array; so
                        //  if "witch_hill" is ids element 5, then idMap has
                        //  "witch_hill" -> 5; if shuffle.order[3] is
                        //  witch_hill, then ple.order[3] = 5.
                        ple.order[jj] = idMap.get(shuffle.order[jj].getID());
                    }
                    //  note that we're going backward through the log, so the
                    //  latest entry is at the start of the list.
                    ples.add(ple);
                    ple = null;
                } else {
                    ++ple.draws;
                }
            }
            dest.writeList("log", ples);
        }
    }

    //hmm, I wonder if this works with tclass...
    public void setDeck(Deck<T> deck) {
        this.deck = deck;
    }
    public Deck<T> getDeck() { return deck; }

    /**
     * Sets the Random to use for shuffling.  You probably only care about this
     * in unit tests.
     *
     * @param rand may be null.
     */
    public void setRandom(Random rand) {
        this.rand = rand;
    }
    /**
     * Returns the Random currently being used for shuffling, or null.
     */
    public Random getRandom() {
        return rand;
    }

    /**
     * Returns the number of cards remaining in the draw pile.
     */
    public int cardsInDrawPile() {
        return (order != null) ? (tos + 1) : 0;
    }

    /**
     * Returns all discards to the deck and shuffles the whole thing.
     */
    public void shuffle() {
        shuffle(false);
    }

    /**
     * Leaves the discards alone, but shuffles the cards currently in the
     * draw deck.
     */
    public void shuffleDrawPile() {
        shuffle(true);
    }

    private void shuffle(boolean drawPileOnly) {
        removeUndoneLogEntries();
        LinkedList<T> newOrder = new LinkedList<>();
        if (rand == null) rand = new Random();
        if (order == null) {
            //  This is the first time we've been shuffled.
            if (drawPileOnly) {
                Log.wtf(LOGBIT, "first-time shuffle() called with drawPileOnly == true");
                return;  //   terrible mistake?
            }
            List<T> tcl = deck.getCards();
            for (T tc : tcl) {
                if (newOrder.isEmpty()) {
                    newOrder.add(tc);
                } else {
                    newOrder.add(rand.nextInt(newOrder.size() + 1), tc);
                }
            }
            tos = newOrder.size() - 1;
        } else {
            if (!drawPileOnly) tos = order.length - 1 - removed;
            int ii = 0;
            //  We're only shuffling up to & including the current top of the
            //  draw pile.
            while (ii <= tos) {
                if (newOrder.isEmpty()) {
                    newOrder.add(order[ii++]);
                } else {
                    newOrder.add(rand.nextInt(newOrder.size() + 1), order[ii++]);
                }
            }
            //  This loop runs if tos < the entire deck; copy over the discards
            //  & removed cards in their current order.
            while (ii < order.length) {
                newOrder.add(order[ii++]);
            }
        }
        T[] na = newArray(newOrder.size());
        na = newOrder.toArray(na);
        if (logpos != DISABLE_LOG) {
            if (log == null) {
                log = new ArrayList<>(na.length * 2);
            }
            log.add(new ShuffleEntry<>(tos, removed, na));
            logpos = log.size() - 1;
        }
        order = na;
        //  tos was either set to na.length - 1 if !drawPileOnly, or remains unchanged

        trimLog(shuffleLogLimit);

        if (Log.isLoggable(LOGBIT, Log.DEBUG)) {
            Log.d(LOGBIT, dump("after shuffle(DrawPileOnly=" + drawPileOnly + "):"));
        }
    }

    /**
     * Returns the top card from the draw pile (or null if the draw pile is
     * empty) and moves it to the discard pile.
     */
    public T draw() {
        if ((order != null) && (tos >= 0)) {
            removeUndoneLogEntries();
            --tos;
            if (logpos != DISABLE_LOG) {
                log.add(new LogEntry<T>(tos));
                ++logpos;
            }
            return order[tos + 1];
        }
        return null;
    }

    /**
     * Returns the top card from the draw pile (or null if the draw pile is
     * empty) but leaves the card there.
     */
    public T peek() {
        if ((order != null) && (tos >= 0)) {
            return order[tos];
        }
        return null;
    }

    /**
     * Returns the top card from the discard pile, or null if the discard pile
     * is empty.
     */
    public T getTopDiscard() {
        if ((order != null) && ((tos + 1) < (order.length - removed))) {
            return order[tos + 1];
        }
        return null;
    }

    /**
     * Enables the undo log.  As a side effect of this, if there's already been
     * at least one shuffle, we build enough log to get back to that shuffle.
     */
    public void enableLog() {
        if (logpos != DISABLE_LOG) return;
        if (order == null) {
            logpos = NO_LOG;
            return;
        }
        //  Well... we're confused if the log doesn't start with a shuffle
        //  entry, so build that and as many draws as it takes to get back to
        //  this point.  This log we're constructing is not necessarily what
        //  *did* happen, but it's the minimum which *could* have happened.
        int ttos = order.length - 1;
        log = new ArrayList<>(order.length * 2);
        log.add(new ShuffleEntry<>(ttos, removed, order));
        while (ttos > tos) {
            log.add(new LogEntry<T>(--ttos));
        }
        logpos = log.size() - 1;
    }

    /**
     * Disables and discards the undo log, if any.
     */
    public void disableLog() {
        logpos = DISABLE_LOG;
        log = null;
    }

    /**
     * Sets the maximum number of shuffles we'll keep in our undo log.  Calling
     * this removes entries beyond the last n shuffles (unless the current undo
     * position is in the area which would be trimmed, in which case we keep as
     * many shuffles as are needed to keep the current undo position).  A value
     * of 0 or less will be treated the same as disableLog(); a positive value
     * will call enableLog().
     */
    public void setShuffleLogLimit(int limit) {
        if (limit <= 0) {
            disableLog();
            return;
        }
        enableLog();
        shuffleLogLimit = limit;
        trimLog(shuffleLogLimit);
    }

    /**
     * Returns the card with the given ID, or null.
     */
    public T findByID(String id) {
//this is silly, should be a method on Deck, which may hash the IDs
        if ((order == null) || (id == null)) return null;
        for (int ii = 0; ii < order.length; ++ii) {
            if (id.equals(order[ii].getID())) return order[ii];
        }
        return null;
    }

    /**
     * Returns true if this changes the deck at all, false if the set of IDs
     * matches what was already removed.  If a card had been removed in an
     * earlier call, but is not included in the set this time, it will be added
     * to the bottom of the discard pile.
     *
     * @param removedIDs must not be null, but may be empty.
     */
    public boolean setRemovedIDs(Set<String> removedIDs) {
        //  Any chance there hasn't been a change?
        if (removedIDs.size() == removed) {
            boolean allGood = true;
            for (int ii = order.length - removed; ii < order.length; ++ii) {
                if (!removedIDs.contains(order[ii].getID())) {
                    allGood = false;
                    break;
                }
            }
            if (allGood) return false;
        }
        //  We're going to create a new shuffle entry, and then walk through
        //  the current order, either putting elements in the removed area at
        //  the top of the stack, or in the main draw/discard area.
        removeUndoneLogEntries();
        T[] target = newArray(order.length);
        int rpos = order.length - removedIDs.size();
        int dpos = 0;
        int tosAdj = 0;
        for (int ii = 0; ii < order.length; ++ii) {
            if (removedIDs.contains(order[ii].getID())) {
                target[rpos++] = order[ii];
                if (ii <= tos) --tosAdj;
            } else {
                target[dpos++] = order[ii];
            }
        }
        tos += tosAdj;
        removed = removedIDs.size();
        order = target;

        if (logpos != DISABLE_LOG) {
            if (log == null) {
                log = new ArrayList<>(target.length * 2);
            }
            log.add(new ShuffleEntry<>(tos, removed, target));
            logpos = log.size() - 1;
            trimLog(shuffleLogLimit);
        }

        if (Log.isLoggable(LOGBIT, Log.DEBUG)) {
            Log.d(LOGBIT, dump("after setRemovedIDs()"));
        }
        return true;
    }

    /**
     * Returns true if the card with the given ID has been removed from the
     * deck.
     */
    public boolean isRemoved(String id) {
        //  linear search, but should be a tiny list
        for (int ii = order.length - removed; ii < order.length; ++ii) {
            if (order[ii].getID().equals(id)) return true;
        }
        return false;
    }

    /**
     * Undoes the last operation (if any) and returns true if anything changed,
     * or false if there was nothing to undo.
     */
    public boolean undo() {
        if ((log == null) || (logpos <= 0)) return false;

        if (log.get(logpos).isShuffle()) {
            //  we have to run backward until we find the previous
            //  ShuffleEntry, because that has the order of the cards
            //  up to this point.
            boolean found = false;
            for (int tlp = logpos - 1; tlp >= 0; --tlp) {
                if (log.get(tlp).isShuffle()) {
                    ShuffleEntry<T> se = (ShuffleEntry<T>) (log.get(tlp));
                    order = se.order;
                    removed = se.removed;
                    found = true;
                    break;
                }
            }
            if (!found) {
throw new NotDoneException("didn't find previous ShuffleEntry");
            }
        }
        --logpos;
        tos = log.get(logpos).tos;
        return true;
    }

    /**
     * Redoes the last operation which was undone (if any) and returns true if
     * anything changed, or false if there was nothing to redo.
     */
    public boolean redo() {
        if ((log == null) || ((logpos + 1) >= log.size())) return false;

        ++logpos;
        if (log.get(logpos) instanceof ShuffleEntry) {
            ShuffleEntry<T> se = (ShuffleEntry<T>)(log.get(logpos));
            order = se.order;
            removed = se.removed;
        }
        tos = log.get(logpos).tos;
        return true;
    }

    /**
     * Returns true if undo() will change the deck state; false if it won't.
     */
    public boolean canUndo() {
        return (log != null) && (logpos > 0);
    }

    /**
     * Returns true if redo() will change the deck state; false if it won't.
     */
    public boolean canRedo() {
        return (log != null) && ((logpos + 1) < log.size());
    }

    /**
     * If we've undone some draws or whatever, and then we "do something" other
     * than redo() (like shuffling), then those undone entries should be removed.
     */
    private void removeUndoneLogEntries() {
        if (log != null) {
            int ii = log.size() - 1;
            while (ii > logpos) {
                log.remove(ii--);
            }
        }
    }

    /**
     * If we're keeping an undo log, this removes entries beyond the last n
     * shuffles.  Values less than 1 are treated as 1.  If the current undo
     * position is in the area which would be trimmed, we keep as many shuffles
     * as are needed to keep the current undo position.
     */
    private void trimLog(int shuffles) {
        if (log == null) return;
        int foundShuffles = 0;
        int pos = log.size() - 1;
        while (pos > 0) {  //  not <= 0, because if we get to 0, we're not trimming
            if (log.get(pos).isShuffle()) {
                ++foundShuffles;
                if ((foundShuffles >= shuffles) && (pos <= logpos)) {
                    //  Trim everything before this.
                    //log.removeRange() is protected, so... lame
                    log = new ArrayList<>(log.subList(pos, log.size()));
                    logpos -= pos;
                    return;
                }
            }
            --pos;
        }
        //  if we're here, the log was already smaller than what they wanted
        //  to trim.
    }

    /**
     * This just returns the number of elements in the log, probably not useful
     * outside unit tests.
     */
    public int getLogSize() {
        return (log != null) ? log.size() : 0;
    }

    /**
     * Returns an array of the cards in the deck & draw pile, in their current
     * order.  Really this is just for a unit test where I wanted to save a copy
     * of a DeckState at a given point, do stuff with it, then restore that
     * state and do other stuff with it.
     *
     * @return a new array containing the Card references, or null if this deck
     *         has never been shuffled.
     */
    public T[] getOrder() {
        return (order != null) ? Arrays.copyOf(order, order.length) : null;
    }

    /**
     * Treated as shuffle(), in which the cards are arranged in the specified
     * order.  Just as getOrder(), this probably is not useful outside unit
     * tests.  Note also that this un-removes any removed cards.
     *
     * @param cards must not be null, must not contain null elements, etc.
     */
    public void setOrder(T[] cards) {
        removeUndoneLogEntries();
        order = Arrays.copyOf(cards, cards.length);
        tos = order.length - 1;
        removed = 0;
        if (logpos != DISABLE_LOG) {
            if (log == null) {
                log = new ArrayList<LogEntry<T>>();
            }
            log.add(new ShuffleEntry<T>(tos, removed, order));
        }
    }

    /**
     * Returns a description of the stack, for debugging
     *
     * @param msg optionally put at the start of the string
     */
    public String dump(String msg) {
        StringBuilder buf = new StringBuilder();
        if (msg != null) buf.append(msg).append("\n");
        for (int ii = order.length - 1; ii >= 0; --ii) {
            if ((removed > 0) && (ii + removed + 1 == order.length)) {
                buf.append("^^^REMOVED^^^\n");
            }
            buf.append("  ").append(order[ii].getID());
            if (tos == ii) buf.append("  <-- TOS");
            buf.append("\n");
        }
        return buf.toString();
    }

    private T[] newArray(int size) {
        //  well, might as well have this warning in one place instead of 3.
        return (T[]) (Array.newInstance(tclass, size));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeTo(new ParcelWrapper(dest, flags));
    }
    public static final CreatorSD<DeckState> CREATOR = new CreatorSD<DeckState>() {
        @Override
        public DeckState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new DeckState(new GsonWrapper(json.getAsJsonObject(), context));
        }
        public DeckState createFromParcel(Parcel in) {
            return new DeckState(new ParcelWrapper(in));
        }
        public DeckState[] newArray(int size) {
            return new DeckState[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
}
