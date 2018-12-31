package com.kuhrusty.morbadscorepad.model;

import com.kuhrusty.morbadscorepad.R;

/**
 */

public enum MapTrait {
    Base(true, R.string.trait_base_label, R.string.trait_base_rule),
    Destroyed(true, R.string.trait_destroyed_label, R.string.trait_destroyed_rule),
    Explored(true, R.string.trait_explored_label, R.string.trait_explored_rule),
    Fungus(true, R.string.trait_fungus_label, R.string.trait_fungus_rule),
    Inside(false, R.string.trait_inside_label, R.string.trait_inside_rule),
    LakePort(false, R.string.trait_lakeport_label, R.string.trait_lakeport_rule),
    Law(true, R.string.trait_law_rule, R.string.trait_law_rule),
    Metaphysical(true, R.string.trait_metaphysical_label, R.string.trait_metaphysical_rule),
    Outside(false, R.string.trait_outside_label, R.string.trait_outside_rule),
    Perilous(true, R.string.trait_perilous_label, R.string.trait_perilous_rule),
    RiverPort(false, R.string.trait_riverport_label, R.string.trait_riverport_rule),
    Settlement(false, R.string.trait_settlement_label, R.string.trait_settlement_rule),
    Void(true, R.string.trait_void_label, R.string.trait_void_rule),
    Woodland(false, R.string.trait_woodland_label, R.string.trait_woodland_rule);

    public static int RECORD_IN_CAMPAIGN_MASK = 0;
    static {
        for (int ii = 0; ii < values().length; ++ii) {
            if (values()[ii].recordInCampaign) {
                RECORD_IN_CAMPAIGN_MASK |= values()[ii].flagBit;
            }
        }
    }

    private int labelResID;
    private int ruleResID;
    private int flagBit;
    /**
     * True if this can be changed on a Location, & therefore needs to be
     * recorded in a campaign.
     */
    public final boolean recordInCampaign;

    MapTrait(boolean recordInCampaign, int labelResID, int ruleResID) {
        this.recordInCampaign = recordInCampaign;
        this.labelResID = labelResID;
        this.ruleResID = ruleResID;
        flagBit = 0x1 << ordinal();
    }

    public int getFlagBit() {
        return flagBit;
    }
}
