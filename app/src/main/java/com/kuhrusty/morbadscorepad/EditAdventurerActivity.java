package com.kuhrusty.morbadscorepad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.kuhrusty.morbadscorepad.model.Adventurer;
import com.kuhrusty.morbadscorepad.model.AdventurerSheet;
import com.kuhrusty.morbadscorepad.model.Deck;
import com.kuhrusty.morbadscorepad.model.Skill;

import java.util.List;

/**
 * WARNING, THIS IS INCOMPLETE AND/OR BROKEN; FOR YOUR SAKE AND MINE, LOOK AWAY
 */
public class EditAdventurerActivity extends EditActivity {
    private static final String LOGBIT = "EditAdventurerActivity";

    public static final String INTENT_ADVENTURER = "EditAdventurerActivity.adventurer";
    /**
     * Just junk that gets returned back to the caller.
     */
    public static final String INTENT_ADVENTURER_NUMBER = "EditAdventurerActivity.adventurerNumber";

    private Integer adventurerNumber = null;

    private AutoCompleteTextView characterChooser;
    private ArrayAdapter<String> skillAdapter;
    private List<Skill> allSkills;
    private List<Skill> filteredSkills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_adventurer);

        allSkills = grepos.getCards(this, config, Deck.SKILL, Skill.class);
        filteredSkills = allSkills;
        List<AdventurerSheet> al = grepos.getAdventurerSheets(this, config);
        String[] aa = new String[al.size()];
        for (int ii = 0; ii < aa.length; ++ii) aa[ii] = al.get(ii).getName();

        //  Fill out some UI stuff
        characterChooser = (AutoCompleteTextView)(findViewById(R.id.character));
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, aa);
        characterChooser.setAdapter(adapter);
        fiddleDropdown(characterChooser);
        //  When someone navigates away from the character selection, we want to
        //  fiddle with the list of available skills.
        characterChooser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    updateSkillList();
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            int ti = intent.getIntExtra(INTENT_ADVENTURER_NUMBER, -1);
            if (ti != -1) adventurerNumber = Integer.valueOf(ti);
            Adventurer as = intent.getParcelableExtra(INTENT_ADVENTURER);
            if (as != null) {
                Log.d(LOGBIT, "got AdventurerRecord for " + as.getName() +
                        ", adventurer number " + adventurerNumber);
                fillUI(as);
            } else {
                Log.d(LOGBIT, "got null AdventurerRecord, adventurer number " +
                        adventurerNumber);
            }
        }
    }

    private void fillUI(Adventurer as) {
        setText(R.id.name, as.getName());
        setText(R.id.character, as.getCharacter());
        setText(R.id.notes, as.getNotes());
        updateSkillList();
    }

    private void loadFromUI(Adventurer as) {
        as.setName(fromUI(R.id.name));
        as.setCharacter(fromUI(R.id.character));
        as.setNotes(fromUI(R.id.notes));
    }

    public void done(View view) {
        Adventurer as = new Adventurer();
        loadFromUI(as);
        Intent rv = new Intent();
        Log.d(LOGBIT, "putting AdventurerRecord " + as.getName());
        rv.putExtra(INTENT_ADVENTURER, as);
        if (adventurerNumber != null) {
            Log.d(LOGBIT, "putting adventurer number " + adventurerNumber);
            rv.putExtra(INTENT_ADVENTURER_NUMBER, adventurerNumber.intValue());
        }
        setResult(RESULT_OK, rv);
        finish();
    }

    private void updateSkillList() {
        Log.d(LOGBIT, "updateSkillList()");
        String name = characterChooser.getText().toString();
        List<AdventurerSheet> al = grepos.getAdventurerSheets(this, config);
        AdventurerSheet found = null;
//        String[] classes = null;
        for (AdventurerSheet ta : al) {
            if (ta.getName().equals(name)) {
                found = ta;
//                classes = ta.getClasses();
                break;
            }
        }
        filteredSkills = Skill.filterSkills(allSkills, found);
Log.d(LOGBIT, "need to update skillAdapter with " + filteredSkills.size() + " new skills");
    }

}
