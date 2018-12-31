package com.kuhrusty.morbadscorepad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * WARNING, THIS IS INCOMPLETE AND/OR BROKEN; FOR YOUR SAKE AND MINE, LOOK AWAY
 *
 * <p>This lets the user choose an existing campaign log entry, or optionally a
 * new campaign.
 *
 * <p>I think I copied this from ChooseCampaignLogActivity, didn't finish
 * updating it to handle adventurers, and then decided it would be better to
 * refactor the common stuff into ChooseActivity.
 */
public class ChooseAdventurerActivity extends ChooseActivity {

    /**
     * The CampaignEntry ID which was selected, returned in the result.  If the
     * user chose a new campaign, this will be null.
     */
    public static final String INTENT_SELECTED_ENTRY = "ChooseAdventurerActivity.selected";

    public ChooseAdventurerActivity() {
        super(R.layout.activity_choose_campaign_log);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_choose_campaign_log);

//  Title should be "select game to copy from", "select game to edit", or "select game to set up"

//        gameConfig = new GameConfiguration(this, PreferenceManager.getDefaultSharedPreferences(this), grepos);

//        Intent intent = null;
//        if (savedInstanceState != null) {
//            allowNew = savedInstanceState.getBoolean(INTENT_ALLOW_NEW, true);
//            defaultEntry = savedInstanceState.getString(INTENT_DEFAULT_ENTRY, null);
//        } else if ((intent = getIntent()) != null) {
//            allowNew = intent.getBooleanExtra(INTENT_ALLOW_NEW, true);
//            defaultEntry = intent.getStringExtra(INTENT_DEFAULT_ENTRY);
//        }

//        TableLayout table = (TableLayout)(findViewById(R.id.choice_list));
//        RowSelectionListener rsl = new RowSelectionListener(findViewById(R.id.ok));

//        List<Campaign> summaries = crepos.getSummaries(this, gameConfig);
//        String prevCampaign = null;
//        for (Campaign cr : summaries) {
//            if (!cr.getCampaignName().equals(prevCampaign)) {
//                //  add a campaign name row.
//                TableRow row = (TableRow)LayoutInflater.from(this).inflate(R.layout.row_choose_campaign, null);
//                ((TextView)row.findViewById(R.id.campaign_name)).setText(cr.getCampaignName());
//                row.setSelected(false);
//                table.addView(row);
//                prevCampaign = cr.getCampaignName();
//            }
//            //  Add a row for this entry.
//            TableRow row = (TableRow)LayoutInflater.from(this).inflate(R.layout.row_choose_campaign_entry, null);
//            ((TextView)row.findViewById(R.id.mission_name)).setText(cr.getMissionName());
//            ((TextView)row.findViewById(R.id.save_date)).setText(cr.getDateString());
////XXX either you need some radio button, or some row selection listener.
//            row.setTag(R.id.campaignID, cr.getID());
//            row.setOnClickListener(rsl);
//            table.addView(row);
//        }
//        if (allowNew) {
//            TableRow row = (TableRow)LayoutInflater.from(this).inflate(R.layout.row_choose_campaign_new, null);
////XXX either you need some radio button, or some row selection listener.
////            row.setTag(R.id.campaignID, INTENT_SELECTED_ENTRY_NEW);
//            row.setOnClickListener(rsl);
//            table.addView(row);
//        }
//        table.requestLayout();  //necessary?
    }

//looks like this was never updated from ChooseCampaignLogActivity
    public void done(View view) {
        String id = (selectedRow != null) ?
                (String)(selectedRow.getTag(R.id.campaignID)) : null;
        Intent rv = new Intent();
        if (id != null) rv.putExtra(INTENT_SELECTED_ENTRY, id);
        setResult(RESULT_OK, rv);
        finish();
    }
}
