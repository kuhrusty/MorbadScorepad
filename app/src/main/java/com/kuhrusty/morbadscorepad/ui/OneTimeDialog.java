package com.kuhrusty.morbadscorepad.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

public abstract class OneTimeDialog {

    /**
     * Pop up a confirmation dialog the first time we run, then save the fact
     * that we did that.
     *
     * @param context
     * @param booleanPrefToCheck the boolean pref key to check.  If it's already
     *                           set to true, this bails; otherwise, once the
     *                           user hits OK in the dialog, this sets the
     *                           preference to true.
     * @param titleResID the title string.
     * @param messageResID  the text of the dialog.
     * @param okResID the text of the OK button.
     * @return true if a dialog was displayed, false if not.
     */
    public static boolean showMaybe(Context context, final String booleanPrefToCheck,
                                    int titleResID, int messageResID, int okResID) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if ((prefs != null) && (!prefs.getBoolean(booleanPrefToCheck, false))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(titleResID);
            builder.setMessage(messageResID);
            builder.setPositiveButton(okResID, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //  Originally this write was done without an
                    //  OnClickListener, after dialog.show(), but that caused
                    //  the dialog to go away if the user rotated the screen
                    //  while it was displayed.
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(booleanPrefToCheck, true);
                    //  apply() instead of commit() because we don't care when it gets
                    //  written to storage.
                    editor.apply();
                }
            });
            builder.setCancelable(false);  //  force them through positive button onClick()
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return false;
    }
}
