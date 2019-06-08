package com.kuhrusty.morbadscorepad.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.kuhrusty.morbadscorepad.model.Card;
import com.kuhrusty.morbadscorepad.model.DeckState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * This just moves the code for handling the removed-card dialog out of
 * DangerDeckActivity.
 *
 * <p>To use this:</p>
 *
 * <pre>
 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
 * RemovedCardListHandler rclh = new RemovedCardListHandler(builder, deck,
 *         this, getString(R.string.some_title), getString(R.string.ok),
 *         getString(R.string.cancel));
 * AlertDialog dialog = builder.create();
 * dialog.show();
 * </pre>
 *
 * <p>And then handle the <code>CardListChangeListener.cardListChanged()</code>
 * callback.</p>
 */
public class RemovedCardListHandler implements DialogInterface.OnMultiChoiceClickListener,
            DialogInterface.OnClickListener {
    private static final String LOGBIT = "RemovedCardListHandler";

    /**
     * When the user clicks OK, cardListChanged() will be passed the new set of
     * deleted card IDs.  (If the user cancels, the listener is never notified.)
     */
    public interface CardListChangeListener {
        void cardListChanged(Set<String> removedCardIDs);
    }

    public RemovedCardListHandler(AlertDialog.Builder builder, DeckState deckState,
                                  CardListChangeListener listener, String title,
                                  String okLabel, String cancelLabel) {
        ArrayList<? extends Card> cards = new ArrayList<>(deckState.getDeck().getCards());
        Collections.sort(cards, Card.NameComparator);

        String[] items = new String[cards.size()];
        cardIDs = new String[items.length];
        checks = new boolean[items.length];
        for (int ii = 0; ii < cards.size(); ++ii) {
            Card tc = cards.get(ii);
            items[ii] = tc.getName();
            if (items[ii] == null) items[ii] = tc.getID();  //  ugh
            cardIDs[ii] = tc.getID();
            checks[ii] = deckState.isRemoved(tc.getID());
            if (checks[ii]) ++checked;
        }

        builder.setTitle(title);
        builder.setMultiChoiceItems(items, checks, this);
        builder.setPositiveButton(okLabel, this);
        if (cancelLabel != null) {
            builder.setNegativeButton(cancelLabel, null);
        }

        this.listener = listener;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int idx, boolean checked) {
        if (checks[idx] != checked) {
            checks[idx] = checked;
            if (checked) ++this.checked; else --this.checked;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Set<String> removed = new TreeSet<>();
        if (checked == cardIDs.length) {
            //  huh, a comedian.  Perhaps they don't understand the interface;
            //  remove no cards.
        } else {
            for (int ii = 0; ii < cardIDs.length; ++ii) {
                if (checks[ii]) removed.add(cardIDs[ii]);
            }
        }
        listener.cardListChanged(removed);
    }

    private CardListChangeListener listener;
    private String[] cardIDs;
    private boolean[] checks;
    private int checked = 0;
}
