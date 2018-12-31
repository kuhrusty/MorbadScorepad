package com.kuhrusty.morbadscorepad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kuhrusty.morbadscorepad.model.Campaign;

import java.util.List;

/**
 * WARNING, THIS IS INCOMPLETE AND/OR BROKEN; FOR YOUR SAKE AND MINE, LOOK AWAY
 *
 * <p>This lets the user choose an existing campaign log entry, or optionally a
 * new campaign.
 */
public class ChooseCampaignLogActivity extends ChooseActivity {
    private static final String LOGBIT = "ChooseCampaignActivity";

    /**
     * On the way in, this is the default (or previously selected) Campaign;
     * on the way out, this is the Campaign selected by the user, or null
     * if they chose to create a new one.
     */
    public static final String INTENT_CAMPAIGN = "ChooseCampaignLogActivity.campaign";

    private Campaign campaignState;

    public ChooseCampaignLogActivity() {
        super(R.layout.activity_choose_campaign_log);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//  Title should be "select game to copy from", "select game to edit", or "select game to set up"

//        gameConfig = new GameConfiguration(this, PreferenceManager.getDefaultSharedPreferences(this), grepos);

        Intent intent = null;
        if (savedInstanceState != null) {
            campaignState = savedInstanceState.getParcelable(INTENT_CAMPAIGN);
//            allowNew = savedInstanceState.getBoolean(INTENT_ALLOW_NEW, true);
//            defaultEntry = savedInstanceState.getString(INTENT_DEFAULT_ENTRY, null);
        } else if ((intent = getIntent()) != null) {
            campaignState = intent.getParcelableExtra(INTENT_CAMPAIGN);
//            allowNew = intent.getBooleanExtra(INTENT_ALLOW_NEW, true);
//            defaultEntry = intent.getStringExtra(INTENT_DEFAULT_ENTRY);
        }

        TableLayout table = (TableLayout)(findViewById(R.id.choice_list));
        RowSelectionListener rsl = new RowSelectionListener(findViewById(R.id.ok));

        List<Campaign> summaries = crepos.getSummaries(this, gameConfig);
        if (summaries != null) {
            String prevCampaign = null;
            for (Campaign cr : summaries) {
                if (!cr.getCampaignName().equals(prevCampaign)) {
                    //  add a campaign name row.
                    TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_choose_campaign, null);
                    ((TextView) row.findViewById(R.id.campaign_name)).setText(cr.getCampaignName());
                    row.setSelected(false);
                    table.addView(row);
                    prevCampaign = cr.getCampaignName();
                }
                //  Add a row for this entry.
                TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_choose_campaign_entry, null);
                ((TextView) row.findViewById(R.id.mission_name)).setText(cr.getMissionName());
                ((TextView) row.findViewById(R.id.save_date)).setText(cr.getDateString());
                row.setTag(R.id.campaignID, cr.getID());
                row.setOnClickListener(rsl);
                table.addView(row);
            }
        }
        if (allowNew) {
            TableRow row = (TableRow)LayoutInflater.from(this).inflate(R.layout.row_choose_campaign_new, null);
//XXX either you need some radio button, or some row selection listener.
//XX            row.setTag(...);
//            row.setTag(R.id.campaignID, INTENT_SELECTED_ENTRY_NEW);
            row.setOnClickListener(rsl);
            table.addView(row);
        }
//select the previously selected campaign, if we have one
        table.requestLayout();  //necessary?
    }

    public void done(View view) {
//XXX presumably wrong to do this I/O on the UI thread.
        String id = (selectedRow != null) ?
                (String)(selectedRow.getTag(R.id.campaignID)) : null;
        Intent rv = new Intent();
Log.d(LOGBIT, "looking at selected row, id " + id + ", existing campaignState == " +
campaignState + "(" + (campaignState != null ? campaignState.getID() : "null") + ")");
        if ((campaignState == null) || (Util.compare(campaignState.getID(), id) != 0)) {
            campaignState = crepos.read(this, gameConfig, id);
Log.d(LOGBIT, "read campaignState " + campaignState);
        }
        rv.putExtra(INTENT_CAMPAIGN, campaignState);
        setResult(RESULT_OK, rv);
        finish();
    }
}
