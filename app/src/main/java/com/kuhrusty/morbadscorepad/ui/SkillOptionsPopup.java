package com.kuhrusty.morbadscorepad.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuhrusty.morbadscorepad.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class SkillOptionsPopup {

    /**
     * Display a popup window with options for SkillListActivity.
     *
     * @param clickedOn the View which was clicked on to trigger this popup.
     * @param haveDilettante true if the Dilettante skill is available to the
     *                       selected adventurer.
     * @param showingDilettante true if we're currently displaying the skills
     *                          which are made available through Dilettante.
     * @param haveHidden true if we have at least one hidden skill.
     * @param showingHidden true if we're currently displaying "hidden" skills.
     * @param listener the Listener to notify when a selection has been made
     *                 and the popup window has been dismissed.
     */
    public static void showPopup(View clickedOn, boolean haveDilettante,
                                 boolean showingDilettante,
                                 boolean haveHidden, boolean showingHidden,
                                 Listener listener) {
        PopupWindow popupWindow = new PopupWindow(clickedOn.getContext());
        PopupClickListener pcl = new PopupClickListener(popupWindow, listener);

        LinearLayout parent = (LinearLayout)LayoutInflater.from(clickedOn.getContext())
                    .inflate(R.layout.popup_skill_options, null, false);

        CheckBox cb = parent.findViewById(R.id.dilettante);
        if (!haveDilettante) {
            cb.setVisibility(View.GONE);
        } else {
            cb.setChecked(showingDilettante);
            cb.setOnClickListener(pcl);
        }

        cb = parent.findViewById(R.id.show_hidden);
        if (!haveHidden) {
            cb.setVisibility(View.GONE);
        } else {
            cb.setChecked(showingHidden);
            cb.setOnClickListener(pcl);
        }

        TextView tv = parent.findViewById(R.id.clear_hidden);
        if (!haveHidden) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setOnClickListener(pcl);
        }

        popupWindow.setContentView(parent);
        popupWindow.setHeight(WRAP_CONTENT);
        popupWindow.setWidth(WRAP_CONTENT);

        // some other visual settings
        popupWindow.setFocusable(true);

        //  ARGHH, EVERY SINGLE TIME I SIT DOWN TO DO SOMETHING WITH ANDROID,
        //  it's the same: 20 minutes of code to add the feature I want, and
        //  then SIX GODDAMN HOURS trying to figure out some brain-damaged UI
        //  behavior.  (I'm only an hour in this time, but I can see the way
        //  this is going to go.)
        //
        //  The showAsDropDown(View) API documentation says:
        //
        //    Display the content view in a popup window anchored to the bottom-
        //    left corner of the anchor view.  If there is not enough room on
        //    screen to show the popup in its entirety, this method tries to
        //    find a parent scroll view to scroll.  If no parent scroll view can
        //    be scrolled, the bottom-left corner of the popup is pinned at the
        //    top left corner of the anchor view.
        //
        //  That's exactly what I *want* to have happen, but it's not; the
        //  popup window happily draws itself mostly off screen, making itself
        //  broken & unusable.
        //
        //  Ah ha ha, someone else pasted that same API comment:
        //  https://stackoverflow.com/questions/52754296/popupwindow-goes-out-screens-bottom-boundary
        parent.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(parent.getMeasuredHeight());
        //  OK, well, that was only like a little over an hour this time.  THIS TIME

        popupWindow.showAsDropDown(clickedOn);
    }

    public interface Listener {
        /**
         * Called when the "dilettante" checkbox is toggled.
         */
        void toggleDilettanteSkills();
        /**
         * Called when the "show hidden skills" checkbox is toggled.
         */
        void toggleShowHiddenSkills();
        /**
         * Called when the "clear hidden skills" item is hit
         */
        void clearHiddenSkills();
    }

    private static class PopupClickListener implements View.OnClickListener {
        private final PopupWindow popupToDismiss;
        private final Listener listener;

        PopupClickListener(PopupWindow popupToDismiss, Listener listener) {
            this.popupToDismiss = popupToDismiss;
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
//this was in the code I originally copied into SpinnerAlternative; commented
//out here because I didn't really take the time to look at what it was adding.
//            // add some animation when a list item was clicked
//            Animation fadeInAnimation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);
//            fadeInAnimation.setDuration(10);
//            view.startAnimation(fadeInAnimation);

            // dismiss the pop up
            popupToDismiss.dismiss();

            if (view.getId() == R.id.dilettante) {
                listener.toggleDilettanteSkills();
            } else if (view.getId() == R.id.show_hidden) {
                listener.toggleShowHiddenSkills();
            } else if (view.getId() == R.id.clear_hidden) {
                listener.clearHiddenSkills();
            } else {
//                Log.e(LOGBIT, "unhandled blah blah")
            }
        }
    }
}
