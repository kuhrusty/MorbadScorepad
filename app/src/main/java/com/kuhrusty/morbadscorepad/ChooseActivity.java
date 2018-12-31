package com.kuhrusty.morbadscorepad;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kuhrusty.morbadscorepad.model.Campaign;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.dao.CampaignRepository;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.dao.RepositoryFactory;

import java.util.List;

/**
 * Common stuff shared by ChooseCampaignActivity and ChooseAdventurerActivity.
 */
public abstract class ChooseActivity extends AppCompatActivity {

    /**
     * If set to false, we won't show a "new entry" option.
     */
    public static final String INTENT_ALLOW_NEW = "ChooseActivity.allowNew";
    /**
     * The default selection (probably what they chose last time).
XXX what is this, the save file name?  some ID.
     */
    public static final String INTENT_DEFAULT_ENTRY = "ChooseActivity.defaultEntry";

    private int layoutID;
    protected String defaultEntry;
    protected boolean allowNew = true;
    protected GameConfiguration gameConfig;
    protected GameRepository grepos = RepositoryFactory.getGameRepository();
    protected CampaignRepository crepos = RepositoryFactory.getCampaignRepository();
    protected View selectedRow = null;

    ChooseActivity(int layoutID) {
        this.layoutID = layoutID;
    }

    protected class RowSelectionListener implements View.OnClickListener {
        private View doneButton;
        public RowSelectionListener(@Nullable View doneButton) {
            this.doneButton = doneButton;
        }
        @Override
        public void onClick(View view) {
            if (selectedRow != null) selectedRow.setSelected(false);
            view.setSelected(true);
            selectedRow = view;
            if ((doneButton != null) && (!doneButton.isEnabled())) {
                doneButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutID);

//  Title should be "select game to copy from", "select game to edit", or "select game to set up"

        gameConfig = new GameConfiguration(this, PreferenceManager.getDefaultSharedPreferences(this), grepos);

        Intent intent = null;
        if (savedInstanceState != null) {
            allowNew = savedInstanceState.getBoolean(INTENT_ALLOW_NEW, true);
        } else if ((intent = getIntent()) != null) {
            allowNew = intent.getBooleanExtra(INTENT_ALLOW_NEW, true);
        }

        TableLayout table = findViewById(R.id.choice_list);
        RowSelectionListener rsl = new RowSelectionListener(findViewById(R.id.ok));

        List<Campaign> summaries = crepos.getSummaries(this, gameConfig);
        String prevCampaign = null;
        for (Campaign cr : summaries) {
            if (!cr.getCampaignName().equals(prevCampaign)) {
                //  add a campaign name row.
                TableRow row = (TableRow)getLayoutInflater().inflate(R.layout.row_choose_campaign, null);
                ((TextView)row.findViewById(R.id.campaign_name)).setText(cr.getCampaignName());
                row.setSelected(false);
                table.addView(row);
                prevCampaign = cr.getCampaignName();
            }
            //  Add a row for this entry.
            TableRow row = (TableRow)getLayoutInflater().inflate(R.layout.row_choose_campaign_entry, null);
            ((TextView)row.findViewById(R.id.mission_name)).setText(cr.getMissionName());
            ((TextView)row.findViewById(R.id.save_date)).setText(cr.getDateString());
            row.setTag(R.id.campaignID, cr.getID());
            row.setOnClickListener(rsl);
            table.addView(row);
        }
        if (allowNew) {
            TableRow row = (TableRow)getLayoutInflater().inflate(R.layout.row_choose_campaign_new, null);
            row.setOnClickListener(rsl);
            table.addView(row);
        }
        table.requestLayout();  //necessary?
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
