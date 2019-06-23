package com.kuhrusty.morbadscorepad.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuhrusty.morbadscorepad.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Android: 20 minutes of coding, 6 goddamn hours of trying to get basic goddamn
 * UI functionality to work.
 *
 * <P>This time I just wanted an icon on my Spinner to indicate that it was
 * clickable.  After hours of screwing with it, I remembered I was using
 * Spinners in another project--how'd I get them working there?  Turns out,
 * there, I had a support class called "SpinnerJunk" with the comment, "I can't
 * believe how much code it takes to do even the simplest operations with a
 * Spinner."  No kidding, buddy.
 *
 * <p>After flailing uselessly for another couple hours (including productive
 * stuff like Googling "why does Spinner suck"), I found this article:
 * <a href="https://www.androidcode.ninja/show-listview-as-drop-down-android/">Show
 * ListView as a Dropdown in Android - a Spinner Alternative</a>, so this is
 * based on that approach.  (Err, it <i>was...</i> but ListView was sucking for
 * me in other ways, so I switched to RecyclerView.  But some of the comments
 * & code below are still from that article.)
 *
 * <p>It is absolutely insane that I am writing this stupid code.  I feel dumb.
 */
public abstract class SpinnerAlternative {
    private static final String LOGBIT = "SpinnerAlternative";

    /**
     * Creates a PopupWindow containing a RecyclerView.
     *
     * @param context
     * @param listenForClicksOn if not null, then an OnClickListener will be
     *                          added which displays the PopupWindow, which is
     *                          kind of the whole point.
     * @param textViewLayoutResID the resource ID for a layout file containing
     *                            a row in the RecyclerView.  The only reason
     *                            this has to be a TextView is that that's what
     *                            we expect to pull out of it in
     *                            TextViewHolderAdapter.onCreateViewHolder();
     *                            you could probably make a version of this
     *                            which takes a RecyclerView.Adapter to handle
     *                            arbitrary views in the row.
     * @param values the strings which will be displayed.  Real classy, I know.
     * @param listener if not null, will be notified when someone clicks on an
     *                 item in the list.  This is also kind of the whole point.
     * @return the PopupWindow, just because that's what the article linked to
     *         above was returning.
     */
    public static PopupWindow createRecyclerViewPopup(Context context,
                                                      View listenForClicksOn,
                                                      int textViewLayoutResID, String[] values,
                                                      ItemSelectionListener listener) {
        RecyclerView listView = new RecyclerView(context);
        //listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(context));

        final PopupWindow popupWindow = new PopupWindow(listView, WRAP_CONTENT, WRAP_CONTENT);

        if (listenForClicksOn != null) {
            listenForClicksOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.showAsDropDown(view, 0, 0);
                }
            });
        }

        listView.setAdapter(new TextViewHolderAdapter(
                new PopupClickListener(popupWindow, listener),
                textViewLayoutResID, values));

        // some other visual settings
        popupWindow.setFocusable(true);

        return popupWindow;
    }

    /**
     * Creates a PopupWindow containing a RecyclerView whose elements will be
     * CheckBoxes.
     *
     * @param context
     * @param listenForClicksOn if not null, then an OnClickListener will be
     *                          added which displays the PopupWindow, which is
     *                          kind of the whole point.
     * @param checkBoxLayoutResID the resource ID for a layout file containing
     *                            a row in the RecyclerView.  The only reason
     *                            this has to be a CheckBox is that that's what
     *                            we expect to pull out of it in
     *                            CheckBoxHolderAdapter.onCreateViewHolder();
     *                            you could probably make a version of this
     *                            which takes a RecyclerView.Adapter to handle
     *                            arbitrary views in the row.
     * @param values the CheckBoxRow values which will be displayed.
     * @param listener if not null, will be notified when someone clicks on an
     *                 item in the list.  This is also kind of the whole point.
     * @return the PopupWindow, just because that's what the article linked to
     *         above was returning.
     */
    public static PopupWindow createRecyclerViewCheckBoxPopup(Context context,
                                                      View listenForClicksOn,
                                                      int checkBoxLayoutResID,
                                                      CheckBoxRow[] values,
                                                      ItemSelectionListener listener) {
        //  really seems like I could've done this with less duplicate code;
        //  this is a complete copy of createRecyclerViewPopup().  See also the
        //  comment on CheckBoxHolder.
        RecyclerView listView = new RecyclerView(context);
        //listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(context));

        final PopupWindow popupWindow = new PopupWindow(listView, WRAP_CONTENT, WRAP_CONTENT);

        if (listenForClicksOn != null) {
            listenForClicksOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.showAsDropDown(view, 0, 0);
                }
            });
        }

        listView.setAdapter(new CheckBoxHolderAdapter(
                new PopupClickListener(popupWindow, listener),
                checkBoxLayoutResID, values));

        // some other visual settings
        popupWindow.setFocusable(true);

        return popupWindow;
    }

    public interface ItemSelectionListener {
        /**
         * Called when an item in the list passed to createRecyclerViewPopup()
         * is clicked on.
         * @param idx the array index clicked on.
         */
        void itemSelected(int idx);
    }

    private static class PopupClickListener implements View.OnClickListener {
        private PopupWindow popupToDismiss;
        private ItemSelectionListener listener;

        PopupClickListener(PopupWindow toDismiss, ItemSelectionListener listener) {
            popupToDismiss = toDismiss;
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            // add some animation when a list item was clicked
            Animation fadeInAnimation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);
            fadeInAnimation.setDuration(10);
            view.startAnimation(fadeInAnimation);

            // dismiss the pop up
            popupToDismiss.dismiss();

            Integer idx = (Integer)(view.getTag(R.id.SpinnerAlternativeIndex));
            if (listener != null) {
                if (idx == null) {
                    Log.w(LOGBIT, "got onClick() with no SpinnerAlternativeIndex!?");
                } else {
                    listener.itemSelected(idx.intValue());
                }
            }
        }
    }

    //  why... why must I write this
    public static class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView view;
        public TextViewHolder(TextView v) {
            super(v);
            view = v;
        }
    }
    //  Sigh... I did try a single ViewHolder<VC extends View> version instead
    //  of having two versions of everything, but it wound up not saving much
    //  code, as TextViewHolderAdapter/CheckBoxHolderAdapter both needed their
    //  own onCreateViewHolder()/onBindViewHolder().
    public static class CheckBoxHolder extends RecyclerView.ViewHolder {
        public CheckBox view;
        public CheckBoxHolder(CheckBox v) {
            super(v);
            view = v;
        }
    }

    //  This was originally pasted from
    //  https://developer.android.com/guide/topics/ui/layout/recyclerview
    public static class TextViewHolderAdapter extends RecyclerView.Adapter<TextViewHolder> {
        private final View.OnClickListener clickListener;
        private final int layoutResID;
        private final String[] values;
        public TextViewHolderAdapter(View.OnClickListener clickListener,
                                     int layoutResID, String[] values) {
            this.clickListener = clickListener;
            this.layoutResID = layoutResID;
            this.values = values;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            TextView tv = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(layoutResID, parent, false);
            tv.setOnClickListener(clickListener);
            return new TextViewHolder(tv);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(TextViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.view.setText(values[position]);
            holder.view.setTag(R.id.SpinnerAlternativeIndex, Integer.valueOf(position));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return values.length;
        }
    }

    public static class CheckBoxHolderAdapter extends RecyclerView.Adapter<CheckBoxHolder> {
        private final View.OnClickListener clickListener;
        private final int layoutResID;
        private final CheckBoxRow[] values;
        public CheckBoxHolderAdapter(View.OnClickListener clickListener,
                                     int layoutResID, CheckBoxRow[] values) {
            this.clickListener = clickListener;
            this.layoutResID = layoutResID;
            this.values = values;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public CheckBoxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            CheckBox cb = (CheckBox) LayoutInflater.from(parent.getContext())
                    .inflate(layoutResID, parent, false);
            cb.setOnClickListener(clickListener);
            return new CheckBoxHolder(cb);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(CheckBoxHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.view.setChecked(values[position].checked);
            holder.view.setText(values[position].label);
            holder.view.setTag(R.id.SpinnerAlternativeIndex, Integer.valueOf(position));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return values.length;
        }
    }

    public static class CheckBoxRow {
        public boolean checked;
        public final String label;
        public final String id;
        public CheckBoxRow(boolean checked, String label, String id) {
            this.checked = checked;
            this.label = label;
            this.id = id;
        }
    }
}
