package com.kuhrusty.morbadscorepad;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kuhrusty.morbadscorepad.model.AdventurerSheet;
import com.kuhrusty.morbadscorepad.model.Card;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.Skill;
import com.kuhrusty.morbadscorepad.model.dao.GameRepository;
import com.kuhrusty.morbadscorepad.model.dao.RepositoryFactory;
import com.kuhrusty.morbadscorepad.ui.SpinnerAlternative;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Displays a list of available characters and the set of skills available for
 * the selected character.
 */
public class SkillListActivity extends AppCompatActivity {
    private static final String LOGBIT = "SkillListActivity";

    private static final String KEY_SELECTED_ADVENTURER = "SkillListActivity.selectedAdventurer";
    private static final String KEY_SELECTED_SKILL = "SkillListActivity.selectedSkill";
    private static final String KEY_SELECTED_XP = "SkillListActivity.selectedXP";
    /**
     * Because we expect the user to navigate away from this activity (like to
     * fiddle with the danger deck), we can't always count on
     * onSaveInstanceState() to store the currently selected adventurer & skill,
     * so use a file when those guys aren't in the saved instance state.  Ugh.
     */
    private static final String STATE_FILENAME = "SkillListActivity.skill.txt";

    private GameConfiguration config;
    private GameRepository grepos = RepositoryFactory.getGameRepository();
    private HashMap<String, AdventurerSheet> characterMap = new HashMap<>();
    private List<Skill> allSkills;
    private List<Skill> filteredSkills;
    private TableLayout table;
    private SkillRowSelectionListener rsl;
    private View selectedRow = null;
    private ImageView skillIV;
    private ImageView masteryIV;
    private TextView adventurerTV;
    private TextView xpTV;

    private String selectedAdventurer;  //  may be null, even when skill is not
    private String selectedSkillID;  //  may be null
    private int selectedXP = 0;

    private class SkillRowSelectionListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String skillID = (String)(view.getTag(R.id.skillID));
            selectedSkillID = skillID;
            loadImage(skillIV, "skill_" + skillID);
            loadImage(masteryIV, "mastery_" + skillID);
            if (selectedRow != null) selectedRow.setSelected(false);
            view.setSelected(true);
            selectedRow = view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_list);
        adventurerTV = findViewById(R.id.character);
        xpTV = findViewById(R.id.xpTextView);

        if (savedInstanceState != null) {
            selectedAdventurer = savedInstanceState.getString(KEY_SELECTED_ADVENTURER);
            selectedSkillID = savedInstanceState.getString(KEY_SELECTED_SKILL);
            selectedXP = savedInstanceState.getInt(KEY_SELECTED_XP);
        }
        if (selectedSkillID == null) {
            //  can we get these guys from file?
            loadStateFromFile();
        }

        if (config == null) {
            config = new GameConfiguration(this, PreferenceManager.getDefaultSharedPreferences(this), grepos);
        }
        allSkills = grepos.getCards(this, config, Deck.SKILL, Skill.class);
        Collections.sort(allSkills, Card.NameComparator);
        //  well, my friends keep complaining because there are duplicates...
        //  but that's because there *are* duplicates!
        grrrRemoveDuplicateSkills(allSkills);
        filteredSkills = allSkills;
        skillIV = findViewById(R.id.skillImage);
        masteryIV = findViewById(R.id.masteryImage);
        rsl = new SkillRowSelectionListener();

        List<AdventurerSheet> al = grepos.getAdventurerSheets(this, config);
        final String[] aa = new String[al.size() + 1];
        aa[0] = getString(R.string.skill_list_all_item);
        for (int ii = 1; ii < aa.length; ++ii) {
            //  We added the "ALL" element, so aa[ii] will be al[ii - 1]
            AdventurerSheet ta = al.get(ii - 1);
            //  well, the names on the sheets are all upper-case; let's do that here
            aa[ii] = ta.getName().toUpperCase();
            characterMap.put(aa[ii], ta);
        }
        String[] xpa = new String[22];  //  max XP on an adventurer sheet is 21
        xpa[0] = getString(R.string.xp_any);
        for (int ii = 1; ii < xpa.length; ++ii) {
            xpa[ii] = Integer.toString(ii);
        }

        //  Fill out some UI stuff
        table = findViewById(R.id.skillList);

        SpinnerAlternative.createRecyclerViewPopup(this, findViewById(R.id.character),
                R.layout.row_adventurer, aa, new SpinnerAlternative.ItemSelectionListener() {
                    @Override
                    public void itemSelected(int idx) {
                        selectedAdventurer = aa[idx];
                        updateSelectedAdventurer();
                        updateSkillList();
                    }
                });
        updateSelectedAdventurer();

        SpinnerAlternative.createRecyclerViewPopup(this, findViewById(R.id.xp),
                R.layout.row_xp, xpa, new SpinnerAlternative.ItemSelectionListener() {
                    @Override
                    public void itemSelected(int idx) {
                        selectedXP = idx;
                        updateSelectedXP();
                        updateSkillList();
                    }
                });
        updateSelectedXP();

        updateSkillList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveStateToFile();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (selectedAdventurer != null) {
            savedInstanceState.putString(KEY_SELECTED_ADVENTURER, selectedAdventurer);
        }
        if (selectedSkillID != null) {
            savedInstanceState.putString(KEY_SELECTED_SKILL, selectedSkillID);
        }
        savedInstanceState.putInt(KEY_SELECTED_XP, selectedXP);
    }

    private void updateSelectedAdventurer() {
        adventurerTV.setText(selectedAdventurer == null ?
                getString(R.string.skill_list_all_item) : selectedAdventurer);
    }

    private void updateSelectedXP() {
        xpTV.setText(selectedXP == 0 ?
                getString(R.string.xp_any) : Integer.toString(selectedXP));
    }

    private void updateSkillList() {
        AdventurerSheet ta = (selectedAdventurer != null) ?
                characterMap.get(selectedAdventurer) : null;
        if ((ta != null) || (selectedXP > 0)) {
            filteredSkills = Skill.filterSkills(allSkills, ta, selectedXP);
        } else {
            filteredSkills = allSkills;
        }
        if (filteredSkills.size() == 0) {
            filteredSkills.add(new Skill(null, getString(R.string.no_skills_available)));
            //  Also need to do something with the images, but... ehh.  An
            //  alternative would be to not let them choose a value lower than
            //  the lowest-XP skill in the otherwise filtered list.
        }

        //  Having a row for each skill in allSkills & toggling their visibility
        //  doesn't work, so remove them all & recreate them.  Ugh.
        table.removeAllViews();
        for (Skill ts : filteredSkills) {
            TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_skill, null);
            //  well, the names on the cards are all upper-case; let's do that here
            ((TextView) row.findViewById(R.id.skill_name)).setText(ts.getName().toUpperCase());
            row.setSelected(false);
            row.setTag(R.id.skillID, ts.getID());
            row.setOnClickListener(rsl);
            table.addView(row);
        }
        table.requestLayout();  //  necessary?
        if ((selectedSkillID == null) || (!restoreSelectedSkill(selectedSkillID))) {
            table.getChildAt(0).callOnClick();
        }
    }

    /**
     * Returns true if the given skill row was found & selected.
     */
    private boolean restoreSelectedSkill(String skillID) {
        if ((skillID == null) || (table == null)) {
            return false;
        }
        for (int ii = 0; ii < table.getChildCount(); ++ii) {
            String tag = (String)(table.getChildAt(ii).getTag(R.id.skillID));
            if ((tag != null) && tag.equals(skillID)) {
                table.getChildAt(ii).callOnClick();
                return true;
            }
        }
        return false;
    }

    //  This assumes two different skill cards with the same name have the same
    //  text etc., which may not be a safe assumption.
    private void grrrRemoveDuplicateSkills(List<Skill> skills) {
        for (int ii = skills.size() - 1; ii > 0; --ii) {
            if (skills.get(ii - 1).getName().equals(skills.get(ii).getName())) {
                skills.remove(ii);
            }
        }
    }

    private void loadImage(ImageView iv, String resID) {
        int id = getResources().getIdentifier(resID, "drawable", getPackageName());
        iv.setImageDrawable(getResources().getDrawable((id != 0) ? id : R.drawable.skill_no_image));
    }

    private void saveStateToFile() {
        Writer out = null;
        try {
            out = new OutputStreamWriter(openFileOutput(STATE_FILENAME, Context.MODE_PRIVATE));
            out.write("1\t" + selectedAdventurer + "\t" + selectedSkillID + "\t" + selectedXP + "\n");
        } catch (IOException ioe) {
            Log.w(LOGBIT, ioe);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioe) {
                    Log.w(LOGBIT, ioe);  //  care *some*, as write may be bad
                }
            }
        }
    }

    /**
     * This is just here to keep some clutter out of onCreate().
     */
    private void loadStateFromFile() {
        BufferedReader in = null;
        String line = null;
        try {
            in = new BufferedReader(new FileReader(openFileInput(STATE_FILENAME).getFD()));
            line = in.readLine();
        } catch (Exception ex) {
            //  we don't really care; the first time this runs, we expect
            //  FileNotFoundException, and after that, if we can't read the
            //  file, they just don't get their state restored.
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    Log.w(LOGBIT, ioe);  //  don't care
                }
            }
        }
        if (line == null) return;
        //  Real classy: we expect, tab-delimited, a version number, an
        //  adventurer, a skill ID, and an XP value.  If we find anything
        //  unexpected, bail, because it really doesn't matter if their state
        //  can't be restored this one time.
        String[] bits = line.split("\t");
        if (bits.length != 4) return;
        selectedAdventurer = bits[1];
        selectedSkillID = bits[2];
        if ((selectedAdventurer != null) && selectedAdventurer.equals("null")) {
            selectedAdventurer = null;
        }
        try {
            selectedXP = Integer.parseInt(bits[3]);
        } catch (NumberFormatException nfe) {
            //  yeah, whatever
        }
    }
}
