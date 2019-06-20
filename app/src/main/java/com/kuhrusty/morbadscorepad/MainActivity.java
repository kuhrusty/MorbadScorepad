package com.kuhrusty.morbadscorepad;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kuhrusty.morbadscorepad.model.Adventurer;
import com.kuhrusty.morbadscorepad.model.Campaign;

public class MainActivity extends AppCompatActivity {
    private static final String LOGBIT = "MainActivity";

    private static final int RESULT_SAVE_CAMPAIGN_SELECTED = RESULT_FIRST_USER + 1;
    private static final int RESULT_EDIT_CAMPAIGN_SELECTED = RESULT_FIRST_USER + 2;
    private static final int RESULT_LOAD_CAMPAIGN_SELECTED = RESULT_FIRST_USER + 3;
    private static final int RESULT_CAMPAIGN_SAVED         = RESULT_FIRST_USER + 4;

    private static final int RESULT_SAVE_ADVENTURER_SELECTED = RESULT_FIRST_USER + 5;
    private static final int RESULT_EDIT_ADVENTURER_SELECTED = RESULT_FIRST_USER + 6;
    private static final int RESULT_LOAD_ADVENTURER_SELECTED = RESULT_FIRST_USER + 7;
    private static final int RESULT_ADVENTURER_SAVED         = RESULT_FIRST_USER + 8;

    private static final int PERM_SAVE_CAMPAIGN = 1234;  //  just made that up
    private static final int PERM_EDIT_CAMPAIGN = 1235;
    private static final int PERM_LOAD_CAMPAIGN = 1236;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//XXX see if we have campaignState in our savedInstanceState (override on-save-state-whatever first)
        updateButtonVisibility();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateButtonVisibility();  //  Preferences may have changed.
        showExpansionWarning();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SAVE_CAMPAIGN_SELECTED) {
            if (resultCode == RESULT_OK) {
                Campaign cs = data.getParcelableExtra(ChooseCampaignLogActivity.INTENT_CAMPAIGN);
                Intent intent = new Intent(this, EditCampaignActivity.class);
                if (cs != null) {
Log.d(LOGBIT, "got Campaign State " + cs.getID());
                    intent.putExtra(EditCampaignActivity.INTENT_CAMPAIGN, cs);
                }
                startActivityForResult(intent, RESULT_CAMPAIGN_SAVED);
            }
        } else if (requestCode == RESULT_EDIT_CAMPAIGN_SELECTED) {
            if (resultCode == RESULT_OK) {
                Campaign cs = data.getParcelableExtra(ChooseCampaignLogActivity.INTENT_CAMPAIGN);
                Intent intent = new Intent(this, EditCampaignActivity.class);
                if (cs != null) {
                    intent.putExtra(EditCampaignActivity.INTENT_CAMPAIGN, cs);
                    intent.putExtra(EditCampaignActivity.INTENT_EDIT_ONLY, true);
                }
                startActivityForResult(intent, RESULT_CAMPAIGN_SAVED);
            }
        } else if (requestCode == RESULT_LOAD_CAMPAIGN_SELECTED) {
            if (resultCode == RESULT_OK) {
Toast.makeText(this, "select load campaign, result " + resultCode, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RESULT_SAVE_ADVENTURER_SELECTED) {
            if (resultCode == RESULT_OK) {
                Adventurer as = data.getParcelableExtra(ChooseAdventurerActivity.INTENT_SELECTED_ENTRY);
                Intent intent = new Intent(this, EditAdventurerActivity.class);
                if (as != null) {
                    intent.putExtra(EditAdventurerActivity.INTENT_ADVENTURER, as);
                }
                startActivityForResult(intent, RESULT_ADVENTURER_SAVED);
            }
        } else if (requestCode == RESULT_EDIT_ADVENTURER_SELECTED) {
            if (resultCode == RESULT_OK) {
                Adventurer as = data.getParcelableExtra(ChooseAdventurerActivity.INTENT_SELECTED_ENTRY);
                Intent intent = new Intent(this, EditAdventurerActivity.class);
                if (as != null) {
                    intent.putExtra(EditAdventurerActivity.INTENT_ADVENTURER, as);
//XXX restore this                    intent.putExtra(EditAdventurerActivity.INTENT_EDIT_ONLY, true);
                }
                startActivityForResult(intent, RESULT_ADVENTURER_SAVED);
            }
        } else if (requestCode == RESULT_LOAD_ADVENTURER_SELECTED) {
            if (resultCode == RESULT_OK) {
Toast.makeText(this, "select load adventurer, result " + resultCode, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode == PERM_SAVE_CAMPAIGN) {
            doSaveCampaign(null);
        } else if (requestCode == PERM_EDIT_CAMPAIGN) {
            doEditCampaign(null);
        } else if (requestCode == PERM_LOAD_CAMPAIGN) {
            doSetUpCampaign(null);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Call when the user wants to save a campaign.  This should launch an
     * Activity which lets them choose an existing campaign to continue, or
     * some new entry.
     */
    public void doSaveCampaign(@Nullable View view) {
        if (checkPermissions(PERM_SAVE_CAMPAIGN)) {
            Intent intent = new Intent(this, ChooseCampaignLogActivity.class);
            intent.putExtra(ChooseCampaignLogActivity.INTENT_ALLOW_NEW, true);
            startActivityForResult(intent, RESULT_SAVE_CAMPAIGN_SELECTED);
        }
        //  else we should get an onRequestPermissionsResult() telling us to
        //  try again
    }

    /**
     * Call when the user wants to edit a saved campaign.  This should launch an
     * Activity which lets them choose an existing campaign to continue.
     */
    public void doEditCampaign(@Nullable View view) {
        if (checkPermissions(PERM_EDIT_CAMPAIGN)) {
            Intent intent = new Intent(this, ChooseCampaignLogActivity.class);
            intent.putExtra(ChooseCampaignLogActivity.INTENT_ALLOW_NEW, false);
            startActivityForResult(intent, RESULT_EDIT_CAMPAIGN_SELECTED);
        }
        //  else we should get an onRequestPermissionsResult() telling us to
        //  try again
    }

    /**
     * Call when the user wants to set up a previously saved campaign.  This
     * should launch an Activity which lets them choose an existing campaign to
     * continue.
     */
    public void doSetUpCampaign(@Nullable View view) {
        if (checkPermissions(PERM_LOAD_CAMPAIGN)) {
            Intent intent = new Intent(this, ChooseCampaignLogActivity.class);
            intent.putExtra(ChooseCampaignLogActivity.INTENT_ALLOW_NEW, false);
            startActivityForResult(intent, RESULT_LOAD_CAMPAIGN_SELECTED);
        }
        //  else we should get an onRequestPermissionsResult() telling us to
        //  try again
    }

    /**
     * Returns true if we have the WRITE_EXTERNAL_STORAGE permission, or false
     * if we needed to request it from the user.  In that case,
     * onRequestPermissionsResult() will be called with the given permRequest,
     * at which point the operation should be repeated.  (Unless they said no,
     * which we probably don't handle very well.)
     */
    private boolean checkPermissions(int permRequest) {
        int perms = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (perms == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            Log.d(LOGBIT, "we don't have write permission; requesting it");
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    permRequest);
            return false;
        }
    }

//    public void doSaveAdventurer(@Nullable View view) {
////XXX need the same permission check stuff above
//        Intent intent = new Intent(this, ChooseAdventurerActivity.class);
//        intent.putExtra(ChooseAdventurerActivity.INTENT_ALLOW_NEW, true);
//        startActivityForResult(intent, RESULT_SAVE_ADVENTURER_SELECTED);
//    }
//    public void doEditAdventurer(@Nullable View view) {
////XXX need the same permission check stuff above
//        Intent intent = new Intent(this, ChooseAdventurerActivity.class);
//        intent.putExtra(ChooseAdventurerActivity.INTENT_ALLOW_NEW, false);
//        startActivityForResult(intent, RESULT_EDIT_ADVENTURER_SELECTED);
//    }
//    public void doSetUpAdventurer(@Nullable View view) {
////XXX need the same permission check stuff above
//        Intent intent = new Intent(this, ChooseAdventurerActivity.class);
//        intent.putExtra(ChooseAdventurerActivity.INTENT_ALLOW_NEW, false);
//        startActivityForResult(intent, RESULT_LOAD_ADVENTURER_SELECTED);
//    }

    /**
     * Called when our skill viewer button is clicked on.  This should launch a
     * skill-viewing window
     */
    public void openSkillList(@Nullable View view) {
        startActivity(new Intent(this, SkillListActivity.class));
    }

    /**
     * Called when our map routes button is clicked on.  This should launch an
     * activity which lets us examine the map.
     */
    public void openMapRoutes(@Nullable View view) {
Toast.makeText(this, "openMapRoutes() doesn't do anything", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when our mission graph button is clicked on.  This should launch
     * an activity which lets us view the graph of mission connections.
     */
    public void openMissionGraph(@Nullable View view) {
Toast.makeText(this, "openMissionGraph() doesn't do anything", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when our danger deck button is clicked on.  This should launch an
     * activity which lets us draw danger cards.
     */
    public void openDangerDeck(@Nullable View view) {
        startActivity(new Intent(this, DangerDeckActivity.class));
    }

    /**
     * Called when our Settings button is clicked on.  This should launch a
     * Settings Activity.
     */
    public void openSettings(@Nullable View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * Called when our Help button is clicked on.  This should launch a help
     * viewer.
     */
    public void openHelp(@Nullable View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_URL, Util.isDeveloper(this) ?
                "file:///android_asset/help/dev_main.html" :
                "file:///android_asset/help/main.html");
        //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    /**
     * This shows/hides buttons depending on whether developer options are
     * enabled.
     */
    private void updateButtonVisibility() {
        //  developer buttons
        int visibility = Util.isDeveloper(this) ? View.VISIBLE : View.GONE;
        setVisibility(visibility, R.id.saveCampaignButton);
        setVisibility(visibility, R.id.editCampaignButton);
        setVisibility(visibility, R.id.loadCampaignButton);
        setVisibility(visibility, R.id.mapRouteButton);
        setVisibility(visibility, R.id.scenarioGraphButton);
    }

    /**
     * Calls setVisibility(visibility) on the given view, if found.
     */
    private void setVisibility(int visibility, int id) {
        View tv = findViewById(id);
        if (tv != null) {
            tv.setVisibility(visibility);
        }
    }

    /**
     * Pop up a confirmation dialog the first time we run, then save the fact
     * that we did that.
     */
    private void showExpansionWarning() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String key = "pref_expansion_warning_shown";  //  not exactly a *preference*...
        if ((prefs != null) && (!prefs.getBoolean(key, false))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.expansions_warning_title);
            builder.setMessage(R.string.expansions_warning);
            builder.setPositiveButton(R.string.expansions_warning_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //  Originally this write was done without an
                    //  OnClickListener, after dialog.show(), but that caused
                    //  the dialog to go away if the user rotated the screen
                    //  while it was displayed.
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(key, true);
                    //  apply() instead of commit() because we don't care when it gets
                    //  written to storage.
                    editor.apply();
                }
            });
            builder.setCancelable(false);  //  force them through positive button onClick()
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
