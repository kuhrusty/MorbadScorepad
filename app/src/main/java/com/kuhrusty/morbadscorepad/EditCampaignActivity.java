package com.kuhrusty.morbadscorepad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kuhrusty.morbadscorepad.model.Adventurer;
import com.kuhrusty.morbadscorepad.model.Campaign;
import com.kuhrusty.morbadscorepad.model.LocationState;
import com.kuhrusty.morbadscorepad.model.Map;
import com.kuhrusty.morbadscorepad.model.MapTrait;
import com.kuhrusty.morbadscorepad.model.Mission;
import com.kuhrusty.morbadscorepad.model.Region;

import java.util.List;

/**
 * WARNING, THIS IS INCOMPLETE AND/OR BROKEN; FOR YOUR SAKE AND MINE, LOOK AWAY
 */
public class EditCampaignActivity extends EditActivity {
    private static final String LOGBIT = "EditCampaignActivity";

    /**
     * The Campaign to copy from; may be null.  In the Intent returned
     * by this Activity, it's the complete entry which was saved.
     */
    public static final String INTENT_CAMPAIGN = "EditCampaignActivity.campaign";
    /**
     * True if we're just editing an existing entry, false if we're creating a
     * new entry based on the given entry ID.
     */
    public static final String INTENT_EDIT_ONLY = "EditCampaignActivity.editOnly";

    private static final int RESULT_ADVENTURER_EDITED = RESULT_FIRST_USER;

    private Campaign cs = null;
    private long startTime;
    private String idToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_campaign);

        List<Mission> ml = grepos.getMissions(this, config);
        String[] ma = new String[ml.size()];
        for (int ii = 0; ii < ma.length; ++ii) ma[ii] = ml.get(ii).getName();

        //  Fill out some UI stuff
        AutoCompleteTextView nameView = (AutoCompleteTextView)(findViewById(R.id.mission));
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, ma);
        nameView.setAdapter(adapter);
        fiddleDropdown(nameView);

        //  well, technically not the ScrollView; the one child which contains
        //  everything else.
        ViewGroup scrollView = findViewById(R.id.odin);  //  all-father, get it
        Map map = grepos.getMap(this, config);
        for (Region tr : map.getRegions()) {
            addRegionTable(tr, scrollView);
        }
        scrollView.requestLayout();  //  necessary?

        startTime = System.currentTimeMillis();
        Intent intent = getIntent();
        if (intent != null) {
            cs = intent.getParcelableExtra(INTENT_CAMPAIGN);
            if (cs != null) {
Log.d(LOGBIT, "got Campaign " + cs.getID());
                fillUI(cs);
                if (intent.getBooleanExtra(INTENT_EDIT_ONLY, false)) {
//                    cr.setID(null);
//                    cr.setTimestamp(System.currentTimeMillis());
                    idToSave = cs.getID();
                    startTime = cs.getTimestamp();
                } else {
                    idToSave = null;
                    cs.setID(null);
                    cs.setTimestamp(startTime);
                }
            }
        }

        if (cs == null) cs = new Campaign();
    }

    private void fillUI(Campaign cr) {
        setText(R.id.campaign, cr.getCampaignName());
        setText(R.id.notes, cr.getNotes());
        setText(R.id.mission, cr.getMissionName());
        for (int ii = 0; ii < 4; ++ii) updateAdventurerIcon(ii);
    }

    private void loadFromUI(Campaign cr) {
        cr.setCampaignName(fromUI(R.id.campaign));
        cr.setNotes(fromUI(R.id.notes));
        cr.setMissionName(fromUI(R.id.mission));
//doom tokens
        //  town/danger levels are kept in sync by our listeners
    }

    public void editAdventurer(View view) {
        Adventurer as = null;
        int an = -1;
        switch (view.getId()) {
            case R.id.adventurer0: an = 0; break;
            case R.id.adventurer1: an = 1; break;
            case R.id.adventurer2: an = 2; break;
            case R.id.adventurer3: an = 3;
        }
        if (an != -1) as = cs.getAdventurer(an);
        Intent intent = new Intent(this, EditAdventurerActivity.class);
        if (as != null) {
            intent.putExtra(EditAdventurerActivity.INTENT_ADVENTURER, as);
        }
        if (an != -1) intent.putExtra(EditAdventurerActivity.INTENT_ADVENTURER_NUMBER, an);
        startActivityForResult(intent, RESULT_ADVENTURER_EDITED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_ADVENTURER_EDITED) {
            if (resultCode == RESULT_OK) {
                int number = data.getIntExtra(EditAdventurerActivity.INTENT_ADVENTURER_NUMBER, -1);
                Adventurer as = data.getParcelableExtra(EditAdventurerActivity.INTENT_ADVENTURER);
                if (number >= 0) {
                    cs.setAdventurer(number, as);
                } else {
                    cs.addAdventurer(as);
                    number = cs.getAdventurerCount() - 1;
                }
                updateAdventurerIcon(number);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateAdventurerIcon(int index) {
        Adventurer as = cs.getAdventurer(index);
        int imgID = (as != null) ? R.drawable.adventurer_default : R.drawable.adventurer_none;
//XXX get the adventurer-specific image
        int btnID = 0;
        if (as != null) {
            switch (index) {
                case 0: btnID = R.id.adventurer0; break;
                case 1: btnID = R.id.adventurer1; break;
                case 2: btnID = R.id.adventurer2; break;
                case 3: btnID = R.id.adventurer3; break;
            }
        }
        if (btnID != 0) {
            ImageButton btn = findViewById(btnID);
            if (btn != null) {
                btn.setImageDrawable(getResources().getDrawable(imgID));
            }
        }
    }

    /**
     * Needed for the UI builder?
     */
    @Override
    public void cancel(View view) {
        super.cancel(view);
    }

    public void done(View view) {
        //  probably wrong to do this on the UI thread.
//        Campaign cr = new Campaign();
//        cs.setID(idToSave);  //  may be null
//        cs.setTimestamp(startTime);
        loadFromUI(cs);
        //  if this returns false, we should expect a call to
        //  onRequestPermissionsResult(), and call write() again.
//        if (crepos.write(this, config, cr)) {
crepos.write(config, cs);
            Intent rv = new Intent();
            rv.putExtra(INTENT_CAMPAIGN, cs);
            setResult(RESULT_OK, rv);
            finish();
//        }
    }

////XXX there's a bad bug here where if they go straight from editing a danger level
////XXX to hitting the done button, we don't get a focus change event on that last
////XXX text view.  Really these things should be number pickers anyway!!
    //  I was able to have one OnItemSelectedListener object which worked for
    //  all town & danger level spinners, but the problem was, when the town
    //  level was selected, we needed to find & clear the corresponding danger
    //  level spinner, which was ugly.  So now we have one instance of this PER
    //  ROW which has a town level (settlements).  Rows which don't have a
    //  visible town spinner use sharedDangerListener, below.
    private class TownDangerListener implements AdapterView.OnItemSelectedListener {
        //  all of these are null if this is the shared instance.
        private String locationName;
        private Spinner town;
        private Spinner danger;
        public TownDangerListener(String locationName, Spinner town, Spinner danger) {
            this.locationName = locationName;
            this.town = town;
            this.danger= danger;
        }
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
            String name = locationName;
            if ((name == null) && ((name = (String)(adapterView.getTag(R.id.locationName))) == null)) {
                Log.w(LOGBIT, "sharedDangerListener ignoring selection on unnamed view");
                return;
            }
Log.d(LOGBIT, "got itemSelected " + pos + " on " + name);
            LocationState loc = cs.getMap().getLocationOrTerritory(name);
            if (loc == null) {
                Log.w(LOGBIT, "ignoring focus change on unknown location \"" + name + "\"!?");
                return;
            }
            if (adapterView == town) {
                if (pos > 0) {
                    loc.setTownLevel(pos);
//Log.d(LOGBIT, "just set " + locationName + " town level to " + val + " (printed " + loc.getPrintedTownLevel() + ")");
                    danger.setSelection(0);
                }
            } else if ((adapterView == danger) || (danger == null)) {  //  danger == null means we're shared
                if (pos > 0) {
                    loc.setDangerLevel(pos);
//Log.d(LOGBIT, "just set " + locationName + " danger level to " + val + " (printed " + loc.getPrintedDangerLevel() + ")");
                    if (town != null) town.setSelection(0);
                }
            } else {
                Log.w(LOGBIT, "confused, onItemSelected() on unknown view " + view);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
    //  This one is shared by all danger level spinners which don't have a
    //  corresponding town spinner.
    private TownDangerListener sharedDangerListener = new TownDangerListener(null, null, null);
//    private AdapterView.OnItemSelectedListener sharedDangerListener = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
//            String name = (String) (adapterView.getTag(R.id.locationName));
//            if (name == null) {
//                Log.w(LOGBIT, "sharedDangerListener ignoring selection on unnamed view");
//                return;
//            }
//            Log.d(LOGBIT, "got itemSelected " + pos + " on " + name);
//            LocationState loc = cs.getMap().getLocationOrTerritory(name);
//            if (loc == null) {
//                Log.w(LOGBIT, "ignoring selection on unknown location \"" + name + "\"!?");
//                return;
//            }
//            if (pos > 0) {
//                loc.setDangerLevel(pos);
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//        }
//    };

//    //  This is used for both town & danger level widgets, with a tag on the
//    //  widget itself to indicate whether it's a town or danger level.
//    private AdapterView.OnItemSelectedListener dangerLevelListener = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
//            String name = (String)(adapterView.getTag(R.id.locationName));
//            if (name == null) {
//                Log.w(LOGBIT, "dangerLevelListener ignoring selection on unnamed view");
//                return;
//            }
//            LocationState loc = cs.getMap().getLocationOrTerritory(name);
//            if (loc == null) {
//                Log.w(LOGBIT, "dangerLevelListener ignoring selection on unknown location \"" + name + "\"");
//                return;
//            }
//            boolean isTown = (adapterView.getTag(R.id.isTown) != null);
//            if (isTown) {
//                loc.setTownLevel(pos > 0 ? pos : loc.getPrintedTownLevel());
//            } else {
//                loc.setDangerLevel(pos > 0 ? pos : loc.getPrintedDangerLevel());
//            }
//            if (pos != 0) {
//                Integer otherID = (Integer)(adapterView.getTag(R.id.otherWidgetID));
//                if (otherID != null) {
//                    Spinner other = findViewById(otherID.intValue());
//                    //  if this triggers the listener on the other, we "should"
//                    //  be OK passing pos = 0
//                    if (other != null) other.setSelection(0);
//                }
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
////Log.d(LOGBIT, "got onNothingSelected(), av tag " + adapterView.getTag(R.id.locationName));
//        }
//    };
    private View.OnClickListener traitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String name = (String)(view.getTag(R.id.locationName));
Log.d(LOGBIT, "onClick() on " + name + "!");
        }
    };

    private void addRegionTable(Region region, ViewGroup parent) {
//Log.d(LOGBIT, "adding region table " + region.getName());
        TableLayout table = (TableLayout)(LayoutInflater.from(this).inflate(R.layout.table_location, null));
        TableRow row = (TableRow)(table.getChildAt(0));
        ((TextView)(row.findViewById(R.id.regionName))).setText(region.getName());
//Log.d(LOGBIT, "  I just set child 0's name to " + region.getName());
        for (int ii = 0; ii < region.getSize(); ++ii) {
            LocationState loc = region.getLocationOrTerritory(ii);
//Log.d(LOGBIT, "adding row for " + loc.getName() + ", town " +
//loc.getTownLevel() + " (printed " + loc.getPrintedTownLevel() + "), danger " +
//loc.getDangerLevel() + " (printed " + loc.getPrintedDangerLevel() + ")");
            row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_location, null);
//Log.d(LOGBIT, "inflated row " + ii);
            Spinner town = row.findViewById(R.id.townLevel);
            Spinner danger = row.findViewById(R.id.dangerLevel);
            TextView name = row.findViewById(R.id.locationName);
            TownDangerListener tdl = sharedDangerListener;
            if ((loc.getDangerLevel() == 0) && (loc.getTownLevel() == 0)) {
                //  this gets its danger level from a territory.
                town.setVisibility(View.INVISIBLE);
                danger.setVisibility(View.INVISIBLE);
            } else {
                if (loc.hasTrait(MapTrait.Settlement)) {
                    town.setSelection((loc.getTownLevel() != loc.getPrintedTownLevel()) ?
                            loc.getTownLevel() : 0);
                    tdl = new TownDangerListener(loc.getName(), town, danger);
//                    town.setTag(R.id.locationName, loc.getName());
//                    town.setTag(R.id.isTown, Integer.valueOf(1));
                    town.setOnItemSelectedListener(tdl);
//Log.w(LOGBIT, "setting otherWidgetIDs " + town.getId() + ", " + danger.getId());
//                    town.setTag(R.id.otherWidgetID, new Integer(danger.getId()));
//                    danger.setTag(R.id.otherWidgetID, new Integer(town.getId()));
                } else {
                    town.setVisibility(View.INVISIBLE);
                    //  sharedDangerListener will need this
                    danger.setTag(R.id.locationName, loc.getName());
                }
                danger.setSelection((loc.getDangerLevel() != loc.getPrintedDangerLevel()) ?
                        loc.getDangerLevel() : 0);
                danger.setOnItemSelectedListener(tdl);
            }
            name.setText(loc.getName());

            LinearLayout traitLayout = row.findViewById(R.id.traitLayout);
            if (loc.traitsDifferFromPrinted()) {
                MapTrait[] mta = MapTrait.values();
                for (int jj = 0; jj < mta.length; ++jj) {
                    if ((mta[ii].recordInCampaign) && loc.hasTrait(mta[ii])) {
                        ImageView iv = new ImageView(this);
//                iv.setImageDrawable(getResources().getDrawable(R.drawable.trait_none_small));
iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_sync_black_24dp));
                        traitLayout.addView(iv);
                    }
                }
            }
            if (traitLayout.getChildCount() == 0) {
                ImageView iv = new ImageView(this);
//                iv.setImageDrawable(getResources().getDrawable(R.drawable.trait_none_small));
                iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_sync_black_24dp));
                traitLayout.addView(iv);
            }
            traitLayout.setTag(R.id.locationName, loc.getName());
            traitLayout.setOnClickListener(traitListener);

            table.addView(row);
//break;
        }
        parent.addView(table);
    }

    /**
     * This is called when one of our doom track icons is clicked on, to make
     * the number-chooser pop up as if <i>it</i> was clicked on.
     */
    public void fireDoomSpinner(View view) {
        Spinner ts = null;
        if (view.getId() == R.id.highlandDoomIcon) ts = findViewById(R.id.highlandDoom);
        else if (view.getId() == R.id.badlandDoomIcon) ts = findViewById(R.id.badlandDoom);
        else if (view.getId() == R.id.lowlandDoomIcon) ts = findViewById(R.id.lowlandDoom);
        else if (view.getId() == R.id.wetlandDoomIcon) ts = findViewById(R.id.wetlandDoom);
        if (ts != null) ts.performClick();
    }
}
