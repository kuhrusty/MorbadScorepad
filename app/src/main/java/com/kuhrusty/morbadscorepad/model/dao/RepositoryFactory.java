package com.kuhrusty.morbadscorepad.model.dao;

import android.support.annotation.Nullable;
import android.util.Log;

import com.kuhrusty.morbadscorepad.model.json.JSONCampaignRepository;
import com.kuhrusty.morbadscorepad.model.json.JSONGameRepository2;

/**
 * This just gives us one place to change the repository implementations.
 */
public class RepositoryFactory {
    public static final String LOGBIT = "RepositoryFactory";

    private static GameRepository gameRepository;
    private static CampaignRepository campaignRepository;

    public static GameRepository getGameRepository() {
        if (gameRepository == null) {
            Log.d(LOGBIT,"creating new GameRepository");
            gameRepository = new CachingGameRepository(new JSONGameRepository2());
        }
        return gameRepository;
    }
    public static CampaignRepository getCampaignRepository() {
        if (campaignRepository == null) {
            Log.d(LOGBIT,"creating new CampaignRepository");
            campaignRepository = new JSONCampaignRepository();
        }
        return campaignRepository;
    }

    /**
     * Pretty much just for use in unit tests which want to specify the
     * implementation to be used throughout the test.
     */
    public static void setGameRepository(@Nullable GameRepository gr) {
        gameRepository = gr;
    }
    /**
     * Pretty much just for use in unit tests which want to specify the
     * implementation to be used throughout the test.
     */
    public static void setCampaignRepository(@Nullable CampaignRepository cr) {
        campaignRepository = cr;
    }
}
