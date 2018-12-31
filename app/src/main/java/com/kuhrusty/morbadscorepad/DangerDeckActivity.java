package com.kuhrusty.morbadscorepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuhrusty.morbadscorepad.model.Danger;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.DeckState;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.dao.RepositoryFactory;

/**
 * Manages a view of the danger deck, with buttons for drawing a new card etc.
 */
public class DangerDeckActivity extends AppCompatActivity {
    private static final String LOGBIT = "DangerDeckActivity";

    private GameConfiguration config;
    private GameRepository grepos = RepositoryFactory.getGameRepository();

    private ImageView dangerIV;
    private TextView dangerIDTV;
    private View drawBtn;
    private View confirmBtn;
    private View undoBtn;
    private View redoBtn;
    private View shuffleDrawBtn;
    private MediaPlayer mediaPlayer;

    private DeckState<Danger> deck;

    private boolean needConfirmDangerUpdate = false;

    //  preference values which we load in onCreate().
    private String audioPref = null;
    private String voicePref = null;
    private boolean confirmPref = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger_deck);

        checkPrefs();
        if (audioPref != null) {
            //  When people hit the volume buttons, we want to change the media
            //  volume, not the ringtone volume.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (config == null) {
            config = new GameConfiguration(this, prefs, grepos);
        }

        if (savedInstanceState != null) {
//XXX issue #12
Log.w(LOGBIT, "need to restore saved deck state");
        }
        if (deck == null) {
            grepos.getCards(this, config, Deck.DANGER, Danger.class);
            deck = new DeckState<>(Danger.class, grepos.getDeck(this, config, Deck.DANGER, Danger.class));
            deck.shuffle();
        }

        dangerIV = findViewById(R.id.cardImage);
        dangerIDTV = findViewById(R.id.dangerText);
        drawBtn = findViewById(R.id.drawButton);
        confirmBtn = findViewById(R.id.confirmDangerButton);
        undoBtn = findViewById(R.id.undoButton);
        redoBtn = findViewById(R.id.redoButton);
        shuffleDrawBtn = findViewById(R.id.shuffleDrawButton);

        confirmBtn.setVisibility(confirmPref ? View.VISIBLE : View.GONE);

        updateUI(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (deck != null) {
//XXX issue #12
Log.w(LOGBIT, "need to save deck state");
//            savedInstanceState.putParcelable(KEY_DECK_STATE, deck);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//XXX issue #12
Log.w(LOGBIT, "need to load deck state");
    }

    @Override
    public void onPause() {
        killSound();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPrefs();
        confirmBtn.setVisibility(confirmPref ? View.VISIBLE : View.GONE);
        //  if they had confirmPref turned on, they pressed drawBtn, drawBtn
        //  became disabled, they opened settings, and turned off confirmPref,
        //  we want drawBtn to be enabled now:
        updateUI(false);
    }

    /**
     * Enables/disables buttons & card image depending on our current state.
     */
    private void updateUI(boolean topDiscardChanged) {
        Danger card = deck.getTopDiscard();
        if (topDiscardChanged) {
            //  Fiddle with the card image.
            String resID = (card != null) ? ("danger_" + card.getID()) : "danger_back";
            int id = getResources().getIdentifier(resID, "drawable", getPackageName());
//  this is copied from SkillListActivity, and uses the Skill "sorry, no
//  image" when no danger card image can be found, which is not quite right.
            dangerIV.setImageDrawable(getResources().getDrawable((id != 0) ? id : R.drawable.skill_no_image));
        }
        drawBtn.setEnabled(!needConfirmDangerUpdate);
        confirmBtn.setEnabled(needConfirmDangerUpdate);
        undoBtn.setEnabled(deck.canUndo());
        redoBtn.setEnabled(deck.canRedo());
        shuffleDrawBtn.setEnabled(deck.canRedo());

        //  temporary diagnostic bit
        dangerIDTV.setText(((card != null) ? card.getID() : "null") + " (" +
                deck.cardsInDrawPile() + " remaining)");
    }

    /**
     * If we're in the middle of playing a sound, stop it.
     */
    private void killSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Draws a card, updates the UI, maybe plays a sound.
     */
    public void doDraw(View view) {
        killSound();
        Danger card = deck.draw();
        if (card == null) deck.shuffle();
        needConfirmDangerUpdate = confirmPref && (card != null);
        updateUI(true);
        if ((card == null) || (audioPref == null)) return;  //  no audio

        String resID;
        if (audioPref.equals("name")) {
//use name only... currently this uses the full audio file.
resID = "danger_" + voicePref + "_" + card.getID();
        } else {
            resID = "danger_" + voicePref + "_" + card.getID();
        }
        int sound = getResources().getIdentifier(resID, "raw", getPackageName());
        if (sound == 0) sound = R.raw.not_done;
        mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
        mediaPlayer.start();
    }

    /**
     * This is just like doDraw(), only without playing a sound or possibly
     * disabling the draw button until the confirm button is pressed.
     */
    public void doSecondaryDraw(View view) {
        killSound();
        if (deck.draw() == null) deck.shuffle();
        updateUI(true);
    }

    public void doConfirmDanger(View view) {
        needConfirmDangerUpdate = false;
        updateUI(false);
    }

    public void doUndo(View view) {
        killSound();
        if (deck.undo()) updateUI(true);  //  well, usually true.
    }

    public void doRedo(View view) {
        if (deck.redo()) updateUI(true);  //  well, usually true.
    }

    public void doShuffleDrawPile(View view) {
        deck.shuffleDrawPile();
        updateUI(false);
    }

    /**
     * Called when we think preferences might have changed.
     */
    private void checkPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        audioPref = (prefs != null) ? prefs.getString("pref_danger_read_sound", null) : null;
        if ((audioPref != null) && audioPref.equals("none")) audioPref = null;

        voicePref = (prefs != null) ? prefs.getString("pref_danger_voice_set", null) : null;
        if (voicePref == null) voicePref = "rusty";  //  well, you know, a reasonable default.

        confirmPref = (prefs != null) && prefs.getBoolean("pref_danger_confirm_updated", false);
        if (!confirmPref) needConfirmDangerUpdate = false;
    }

    //  originally I had a button which did this; I removed that when I added
    //  the full Settings button.
    // /**
    // * "full", "name", or null.
    // */
    //private void setSoundPref(String sound) {
    //    if (sound == null) sound = "none";
    //    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    //    SharedPreferences.Editor editor = prefs.edit();
    //    editor.putString("pref_danger_read_sound", sound);
    //    editor.commit();
    //}

    /**
     * Called when our Settings button is clicked on.  This should launch a
     * Settings Activity.
     */
    public void openSettings(@Nullable View view) {
//XXX really it would be nice to open just the danger deck settings, not the whole enchilada.
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * Called when our Help button is clicked on.  This should launch a help
     * viewer.
     */
    public void openHelp(@Nullable View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_URL,
                "file:///android_asset/help/danger.html");
        //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
