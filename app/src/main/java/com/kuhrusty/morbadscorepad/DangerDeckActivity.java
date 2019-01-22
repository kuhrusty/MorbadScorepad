package com.kuhrusty.morbadscorepad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kuhrusty.morbadscorepad.model.Danger;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.DeckState;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.dao.RepositoryFactory;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Manages a view of the danger deck, with buttons for drawing a new card etc.
 */
public class DangerDeckActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    private static final String LOGBIT = "DangerDeckActivity";

    private static final String KEY_CONFIG = "config";
    private static final String KEY_DECK = "deck";

    private static final String DECK_STATE_FILENAME = "DangerDeckActivity.deck.json";

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

    //  whether the last first-party-danger-card-draw was a RESHUFFLE DANGER
    //  DECK card.  This does not work real well with "undo"; if you draw one,
    //  and hit undo, and draw again, we probably reshuffle first.
    private boolean lastPrimaryWasReshuffle = false;
    private boolean needConfirmDangerUpdate = false;

    //  preference values which we load in onCreate().
    private String audioPref = null;
    private String voicePref = null;
    private boolean confirmPref = false;

    /**
     * Just for fun... regardless of whether they have sound turned on or off,
     * if they tap the card image, play or stop the card's audio.
     */
    private final View.OnTouchListener soundFiddler = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(LOGBIT, "got main image touch, mediaPlayer == " + mediaPlayer);
                if (mediaPlayer != null) {
                    killSound();
                } else {
                    playCardSound(false);
                }
                return true;
            }
            return false;
        }
    };

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

        if ((config == null) && (savedInstanceState != null)) {
            config = savedInstanceState.getParcelable(KEY_CONFIG);
            if (config != null) Log.d(LOGBIT, "onCreate() using saved config");
        }
//hey, now that config is Parcelable, you oughtta check the Intent to see if we
//passed one in.
        if (config == null) {
            Log.d(LOGBIT, "onCreate() creating new config");
            config = new GameConfiguration(this, prefs, grepos);
        }

        if ((deck == null) && (savedInstanceState != null)) {
            try {
                //  I'm not totally positive that this approach works; in my
                //  tests, the same instance is being passed straight across in
                //  the Bundle, rather than being serialized & having a new
                //  instance deserialized.  At least it's working in unit tests
                //  with a mocked context & config...
                Util.setContextAndConfig(this, config);
                deck = savedInstanceState.getParcelable(KEY_DECK);
            } finally {
                Util.setContextAndConfig(null, null);
            }
            if (deck != null) Log.d(LOGBIT, "onCreate() using saved deck state");
        }
        if (deck == null) {
            //  See if we can load our deck state from file
            loadDeckStateFromFile();
            if (deck != null) Log.d(LOGBIT, "onCreate() using deck state from file");
        }
        if (deck == null) {
            Log.d(LOGBIT, "onCreate() creating new deck state");
            grepos.getCards(this, config, Deck.DANGER, Danger.class);
            deck = new DeckState<>(Danger.class, grepos.getDeck(this, config, Deck.DANGER, Danger.class));
            deck.shuffle();
        }

        dangerIV = findViewById(R.id.cardImage);
        dangerIV.setOnTouchListener(soundFiddler);
        dangerIDTV = findViewById(R.id.dangerText);
        drawBtn = findViewById(R.id.drawButton);
        confirmBtn = findViewById(R.id.confirmDangerButton);
        undoBtn = findViewById(R.id.undoButton);
        redoBtn = findViewById(R.id.redoButton);
        shuffleDrawBtn = findViewById(R.id.shuffleDrawButton);

        confirmBtn.setVisibility(confirmPref ? View.VISIBLE : View.GONE);

        updateUI(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveDeckStateToFile();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (config != null) savedInstanceState.putParcelable(KEY_CONFIG, config);
        if (deck != null) savedInstanceState.putParcelable(KEY_DECK, deck);
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
     * Plays the sound for the card at the top of the discard pile, if any.  If
     * checkAudioPref is true, we'll check audioPref to see whether we should
     * play the full sound or just the name; if false, we'll play the full
     * sound.
     */
    private void playCardSound(boolean checkAudioPref) {
        Danger card = deck.getTopDiscard();
        if (card == null) return;
        String resID = "danger_" + voicePref + "_" + card.getID();
        if (checkAudioPref && audioPref.equals(SettingsActivity.PREF_DANGER_READ_SOUND_VALUE_NAME)) {
            //  use name only... currently this is ignored, and uses the full
            //  audio file.
            Log.w(LOGBIT, "audioPref \"" +
                    SettingsActivity.PREF_DANGER_READ_SOUND_VALUE_NAME +
                    "\" doesn't do anything");
            //resID = ...
        }
        int sound = getResources().getIdentifier(resID, "raw", getPackageName());
        if (sound == 0) sound = R.raw.not_done;
        //  was getApplicationContext() instead of this
        mediaPlayer = MediaPlayer.create(this, sound);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
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
     * Called when our MediaPlayer finishes playing its current sound.
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.release();
        if (this.mediaPlayer == mediaPlayer) this.mediaPlayer = null;
    }

    /**
     * Draws a card, updates the UI, maybe plays a sound.
     */
    public void doDraw(View view) {
        killSound();
        if (lastPrimaryWasReshuffle) {
            deck.shuffle();
            lastPrimaryWasReshuffle = false;
        }
        Danger card = deck.draw();
        if (card == null) {
            deck.shuffle();
        } else if (card.isReshuffle()) {
            lastPrimaryWasReshuffle = true;
        }
        needConfirmDangerUpdate = confirmPref && (card != null);
        updateUI(true);
        if ((card != null) && (audioPref != null)) {
            playCardSound(true);
        }
    }

    /**
     * This is just like doDraw(), only without playing a sound or possibly
     * disabling the draw button until the confirm button is pressed.
     */
    public void doSecondaryDraw(View view) {
        killSound();
        if (deck.draw() == null) {
            deck.shuffle();
        }
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

    public void doShuffleAll(View view) {
        deck.shuffle();
        updateUI(true);
    }

    /**
     * Called when we think preferences might have changed.
     */
    private void checkPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        audioPref = (prefs != null) ? prefs.getString(SettingsActivity.PREF_DANGER_READ_SOUND,
                SettingsActivity.PREF_DANGER_READ_SOUND_VALUE_FULL) : null;
        if ((audioPref != null) && audioPref.equals(SettingsActivity.PREF_DANGER_READ_SOUND_VALUE_NONE)) {
            audioPref = null;
        }

        voicePref = (prefs != null) ? prefs.getString(SettingsActivity.PREF_DANGER_VOICE_SET,
                SettingsActivity.PREF_DANGER_VOICE_SET_DEFAULT) : null;
        if (voicePref == null) voicePref = SettingsActivity.PREF_DANGER_VOICE_SET_DEFAULT;

        confirmPref = (prefs != null) && prefs.getBoolean(SettingsActivity.PREF_DANGER_CONFIRM_UPDATED, false);
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

    private void saveDeckStateToFile() {
        Gson gson = JSONGameRepository.newGsonBuilder().create();
        Writer fw = null;
        try {
            fw = new OutputStreamWriter(openFileOutput(DECK_STATE_FILENAME, Context.MODE_PRIVATE));
            gson.toJson(deck, fw);
        } catch (IOException ioe) {
            Log.w(LOGBIT, ioe);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ioe) {
                    Log.w(LOGBIT, ioe);  //  care *some*, as write may be bad
                }
            }
        }
    }

    /**
     * This is just here to keep some clutter out of onCreate().
     */
    private void loadDeckStateFromFile() {
        Gson gson = JSONGameRepository.newGsonBuilder().create();
        FileReader fr = null;
        try {
            Util.setContextAndConfig(this, config);
            fr = new FileReader(openFileInput(DECK_STATE_FILENAME).getFD());
            deck = gson.fromJson(fr, DeckState.class);
        } catch (Exception ex) {
            if (!(ex instanceof FileNotFoundException)) {
                Log.e(LOGBIT, "couldn't restore danger deck from file", ex);
                Toast.makeText(this, R.string.err_restore_bad_danger_deck,
                        Toast.LENGTH_LONG).show();
            }
        } finally {
            Util.setContextAndConfig(null, null);
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException ioe) {
                    Log.w(LOGBIT, ioe);  //  don't care
                }
            }
        }
    }
}
