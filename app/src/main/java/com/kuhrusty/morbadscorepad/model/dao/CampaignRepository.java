package com.kuhrusty.morbadscorepad.model.dao;

import android.content.Context;

import com.kuhrusty.morbadscorepad.model.Campaign;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;

import java.util.List;

/**
 * For saving & loading user-entered information about the state of their
 * campaign.
 */
public interface CampaignRepository {

    /**
     * Returns a list of all Campaign objects, but only partially filled
     * in: just the names, dates, etc., enough to display a list for choosing.
     */
    List<Campaign> getSummaries(Context context, GameConfiguration gameConfig);
    Campaign read(Context context, GameConfiguration config, String fileID);

    /**
     * Make sure you have PERM_REQUEST_WRITE_EXTERNAL before calling this.
     */
    void write(GameConfiguration config, Campaign cr);
}
