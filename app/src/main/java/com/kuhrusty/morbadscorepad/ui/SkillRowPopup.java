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

public abstract class SkillRowPopup {

    /**
     * Display a popup window with options for a specific skill row.
     *
     * @param clickedOn the View which was clicked on to trigger this popup.
     * @param skillID the skill in question
     * @param skillName the name of the skill to display
     * @param highlighted true if this skill row is currently highlighted
     * @param hidden true if this skill row is currently hidden
     * @param listener the Listener to notify when a selection has been made
     *                 and the popup window has been dismissed.
     */
    public static void showPopup(View clickedOn, String skillID, String skillName,
                boolean highlighted, boolean hidden, Listener listener) {
        PopupWindow popupWindow = new PopupWindow(clickedOn.getContext());
        PopupClickListener pcl = new PopupClickListener(popupWindow, skillID, listener);

        LinearLayout parent = (LinearLayout)LayoutInflater.from(clickedOn.getContext())
                    .inflate(R.layout.popup_skill_row, null, false);

        TextView tv = parent.findViewById(R.id.skill_name);
        tv.setText(skillName);

        CheckBox cb = parent.findViewById(R.id.highlight);
        cb.setChecked(highlighted);
        cb.setOnClickListener(pcl);

        cb = parent.findViewById(R.id.hide);
        cb.setChecked(hidden);
        cb.setOnClickListener(pcl);

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
         * Called when the "highlight" checkbox is toggled.
         */
        void toggleHighlightSkill(String skillID);
        /**
         * Called when the "hide" checkbox is toggled.
         */
        void toggleHideSkill(String skillID);
    }

    private static class PopupClickListener implements View.OnClickListener {
        private final PopupWindow popupToDismiss;
        private final String skillID;
        private final Listener listener;


        PopupClickListener(PopupWindow popupToDismiss, String skillID,
                           Listener listener) {
            this.popupToDismiss = popupToDismiss;
            this.skillID = skillID;
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

            if (view.getId() == R.id.highlight) {
                listener.toggleHighlightSkill(skillID);
            } else if (view.getId() == R.id.hide) {
                listener.toggleHideSkill(skillID);
            } else {
//                Log.e(LOGBIT, "unhandled blah blah")
            }
        }
    }
}
