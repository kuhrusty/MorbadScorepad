package com.kuhrusty.morbadscorepad.model;

import android.util.Log;

import com.kuhrusty.morbadscorepad.NotDoneException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This keeps track of the order of a deck of cards and its discard pile, and an
 * optional undo log of changes to that order.
 */
public class DeckState<T extends Card> {
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
    }
    private static class ShuffleEntry<T> extends LogEntry<T> {
        final T[] order;
        ShuffleEntry(int tos, T[] order) {
            super(tos);
            this.order = order;
        }
    }

    final private Class<T> tclass;
    private Deck<T> deck;
    //  later entries are appended to the log.
    private List<LogEntry<T>> log;
    private int logpos = NO_LOG;
    private T[] order;  //  reference to order from last ShuffleEntry
    private int tos;  //  copied from last LogEntry

    private Random rand;

    //public DeckState(Class<T> tclass) {
    //    this.tclass = tclass;
    //}
    public DeckState(Class<T> tclass, Deck<T> deck) {
        this.tclass = tclass;
        this.deck = deck;
    }
    //hmm, I wonder if this works with tclass...
    public void setDeck(Deck<T> deck) {
        this.deck = deck;
    }
    public void setRandom(Random rand) {
        this.rand = rand;
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
            if (!drawPileOnly) tos = order.length - 1;
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
            //  in their current order.
            while (ii < order.length) {
                newOrder.add(order[ii++]);
            }
        }
        T[] na = (T[])(Array.newInstance(tclass, newOrder.size()));
        na = newOrder.toArray(na);
        if (logpos != DISABLE_LOG) {
            if (log == null) {
                log = new ArrayList<>(na.length * 2);
            }
            log.add(new ShuffleEntry<>(tos, na));
            logpos = log.size() - 1;
        }
        order = na;
        //  tos was either set to na.length - 1 if !drawPileOnly, or remains unchanged

        if (Log.isLoggable(LOGBIT, Log.DEBUG)) {
            StringBuilder buf = new StringBuilder();
            buf.append("after shuffle(DrawPileOnly=").append(drawPileOnly).append(":\n");
            for (int ii = order.length - 1; ii >= 0; --ii) {
                buf.append("  ").append(order[ii].getID());
                if (tos == ii) buf.append("  <-- TOS");
                buf.append("\n");
            }
            Log.d(LOGBIT, buf.toString());
        }
    }

    /**
     * Returns the top card from the draw pile (or null if the draw pile is
     * empty) and moves it to the discard pile.
     */
    public T draw() {
        if ((order != null) && (tos >= 0)) {
            //  trim the log if necessary
            while ((log != null) && (log.size() > (logpos + 1))) {
                log.remove(log.size() - 1);
            }
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
        if ((order != null) && ((tos + 1) < order.length)) {
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
        log.add(new ShuffleEntry<>(ttos, order));
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
     * Undoes the last operation (if any) and returns true if anything changed,
     * or false if there was nothing to undo.
     */
    public boolean undo() {
        if ((log == null) || (logpos <= 0)) return false;

        if (log.get(logpos) instanceof ShuffleEntry) {
            //  we have to run backward until we find the previous
            //  ShuffleEntry, because that has the order of the cards
            //  up to this point.
            boolean found = false;
            for (int tlp = logpos - 1; tlp >= 0; --tlp) {
                if (log.get(tlp) instanceof ShuffleEntry) {
                    ShuffleEntry<T> se = (ShuffleEntry<T>) (log.get(tlp));
                    order = se.order;
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
}
