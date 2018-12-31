package com.kuhrusty.morbadscorepad;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Displays a list of available characters and the set of skills available for
 * the selected character.
 */
public class SkillListActivity extends AppCompatActivity {
    private static final String LOGBIT = "SkillListActivity";

    private static final String KEY_SELECTED_SKILL = "SkillListActivity.selectedSkill";

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
    private String allSkillsItemLabel;

    private class SkillRowSelectionListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String skillID = (String)(view.getTag(R.id.skillID));
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
        allSkillsItemLabel = getString(R.string.skill_list_all_item);

        String selectedSkill = null;
        if (savedInstanceState != null) {
            selectedSkill = savedInstanceState.getString(KEY_SELECTED_SKILL);
Log.d(LOGBIT, "onCreate() got savedInstanceState with selected skill " + selectedSkill);
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
        String[] aa = new String[al.size() + 1];
        aa[0] = allSkillsItemLabel;
        for (int ii = 1; ii < aa.length; ++ii) {
            //  We added the "ALL" element, so aa[ii] will be al[ii - 1]
            AdventurerSheet ta = al.get(ii - 1);
            //  well, the names on the sheets are all upper-case; let's do that here
            aa[ii] = ta.getName().toUpperCase();
            characterMap.put(aa[ii], ta);
        }

        //  Fill out some UI stuff
        table = findViewById(R.id.skillList);
        Spinner characterChooser = findViewById(R.id.character);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, aa) {
                    //  ugh, all this is just to set the $%#$!! font!?
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View tv = super.getView(position, convertView, parent);
                        ((TextView)tv).setTextAppearance(SkillListActivity.this, R.style.AdventurerSpinner);
                        return tv;
                    }
                    @Override
                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View tv = super.getDropDownView(position, convertView, parent);
                        ((TextView)tv).setTextAppearance(SkillListActivity.this, R.style.AdventurerSpinner);
                        return tv;
                    }
        };
        characterChooser.setAdapter(adapter);
        characterChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Log.d(LOGBIT, adapterView.getSelectedItem().toString() + " selected");
                updateSkillList(adapterView.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(LOGBIT, "nothing selected");
            }
        });
        restoreSelectedSkill(selectedSkill);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (selectedRow != null) {
Log.d(LOGBIT, "onSaveInstanceState() saving selected skill " + selectedRow.getTag(R.id.skillID));
            savedInstanceState.putString(KEY_SELECTED_SKILL,
                    (String)(selectedRow.getTag(R.id.skillID)));
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        restoreSelectedSkill(savedInstanceState.getString(KEY_SELECTED_SKILL));
    }

    private void updateSkillList(String selectedName) {
Log.d(LOGBIT, "updateSkillList(" + selectedName + ") hit");
        AdventurerSheet ta = ((selectedName != null) &&
                         (!selectedName.equals(allSkillsItemLabel))) ?
                characterMap.get(selectedName) : null;
        if (ta != null) {
            filteredSkills = Skill.filterSkills(allSkills, ta);
        } else {
            filteredSkills = allSkills;
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
        table.getChildAt(0).callOnClick();
    }

    private void restoreSelectedSkill(String skillID) {
Log.d(LOGBIT, "restoreSelectedState(" + skillID + ") hit, table == " + table);
        if ((skillID == null) || (table == null)) {
            return;
        }
        for (int ii = 0; ii < table.getChildCount(); ++ii) {
            String tag = (String)(table.getChildAt(ii).getTag(R.id.skillID));
Log.d(LOGBIT, "  " + tag);
            if ((tag != null) && tag.equals(skillID)) {
Log.d(LOGBIT, "    CALLING ON CLICK");
                table.getChildAt(ii).callOnClick();
                break;
            }
        }
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
}
