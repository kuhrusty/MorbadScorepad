package com.kuhrusty.morbadscorepad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.dao.CampaignRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.dao.RepositoryFactory;

/**
 * WARNING, THIS IS INCOMPLETE AND/OR BROKEN; FOR YOUR SAKE AND MINE, LOOK AWAY
 */
public abstract class EditActivity extends AppCompatActivity {

    protected GameConfiguration config;
    protected GameRepository grepos = RepositoryFactory.getGameRepository();
    protected CampaignRepository crepos = RepositoryFactory.getCampaignRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = new GameConfiguration(this, PreferenceManager.getDefaultSharedPreferences(this), grepos);
    }

    protected void fiddleDropdown(final AutoCompleteTextView textView) {
        textView.setSingleLine();
        textView.setThreshold(0);  //  snarl, treated as 1
        textView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS |
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        textView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //  this is because setThreshold(0) is treated as 1; we want the
        //  dropdown even when there's nothing entered yet.
//            textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//Log.w(LOGBIT, "onFocusChange(), isPopupShowing " + textView.isPopupShowing() + ", hasFocus " + hasFocus);
//                    if (hasFocus)
//                        textView.showDropDown();
//                    }
//            });
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//Log.w(LOGBIT, "onTouch(), isPopupShowing " + textView.isPopupShowing() + ", event " + event);
                if ((event.getAction() == MotionEvent.ACTION_DOWN) && (!textView.isPopupShowing())) {
                    textView.showDropDown();
                }
                return false;
            }
        });
    }

    protected void setText(int textViewID, String txt) {
        TextView tv = (TextView)(findViewById(textViewID));
        if (tv != null) tv.setText(txt != null ? txt : "");
    }
    //  well, that's awkward... there's already a getText(int) on Context.
    protected String fromUI(int textViewID) {
        TextView tv = (TextView)(findViewById(textViewID));
        return (tv != null) ? tv.getText().toString() : null;
    }

    @Override
    public void onBackPressed() {
        cancel(null);
    }

    public void cancel(View view) {
        //  copied from https://stackoverflow.com/questions/2257963
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.confirm_close_title)
                .setMessage(R.string.confirm_close_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }
}
