package com.kuhrusty.morbadscorepad.model.json;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.kuhrusty.morbadscorepad.model.Campaign;
import com.kuhrusty.morbadscorepad.model.GameConfiguration;
import com.kuhrusty.morbadscorepad.model.Map;
import com.kuhrusty.morbadscorepad.model.dao.CampaignRepository;
import com.kuhrusty.morbadscorepad.model.dao.RepositoryFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A half-completed CampaignRepository implementation which uses Gson to write
 * JSON files.
 */
public class JSONCampaignRepository implements CampaignRepository {
    private static final String LOGBIT = "JSONCampaignRepository";

    private static final String CAMPAIGN_INDEX_FILE = "campaignIndex.txt";

    @Override
    public List<Campaign> getSummaries(Context context, GameConfiguration config) {
        List<Campaign> rv = new ArrayList<>();

        //  Read from the index file.  This is kind of dumb; what we *should* do
        //  is pop up a busy dialog, and on a non-UI thread, read just as much
        //  as we need from each JSON file, using the streaming API which should
        //  let us bail as soon as we have what we need.  And then not bother
        //  with the stupid index file in the first place, as odds are good that
        //  it's going to get broken/out-of-sync/etc.
Log.d(LOGBIT, "document directory: " + Environment.getExternalStoragePublicDirectory(
Environment.DIRECTORY_DOCUMENTS));
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), config.getDataDirectory());
        if (!dir.exists()) {
            Log.d(LOGBIT, dir.getAbsolutePath() + " doesn't exist, returning empty list");
            return rv;
        }
        File file = new File(dir, CAMPAIGN_INDEX_FILE);
        if (!file.exists()) {
            Log.d(LOGBIT, CAMPAIGN_INDEX_FILE + " doesn't exist, returning empty list");
            return rv;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line = null;
            int lineNum = 0;
            while ((line = in.readLine()) != null) {
                ++lineNum;
                if (line.startsWith("//")) continue;
                String ta[] = line.split("\\t", 4);
                if (ta.length == 4) {
                    try {
                        rv.add(new Campaign(ta[0], ta[1], ta[2], Long.parseLong(ta[3])));
                    } catch (NumberFormatException nfe) {
                        Log.e(LOGBIT, "failed to parse " +
                                file.getAbsolutePath() + " line " + lineNum, nfe);
                    }
                } else {
                    Log.e(LOGBIT, "failed to parse " +
                            file.getAbsolutePath() + " line " + lineNum +
                            ": expected 4 elements, got " + ta.length);
                }
            }
        } catch (IOException ioe) {
            Log.e(LOGBIT, "failed to read " + file.getAbsolutePath(), ioe);
            return null;
        }
        Collections.sort(rv, Campaign.NameTimestampComparator);
        return rv;
    }

    @Override
    public Campaign read(Context context, GameConfiguration config, String fileID) {
        Map printedMap = RepositoryFactory.getGameRepository().getMap(context, config);

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), config.getDataDirectory());
//        if (!dir.exists()) {
//        }
        File file = new File(dir, fileID + ".json");
//        if (!file.exists()) {
//            Log.e(LOGBIT, file.getAbsolutePath() + " doesn't exist");
//            return null;
//        }
        Gson gson = JSONGameRepository.newGsonBuilder().create();
        Campaign rv = null;
        try {
            rv = gson.fromJson(new FileReader(file), Campaign.class);
        } catch (FileNotFoundException fnfe) {
            Log.e(LOGBIT, "failed to read " + file.getName(), fnfe);
            return null;
        }
        rv.setPrintedMap(printedMap);
        return rv;
    }

    @Override
    public void write(GameConfiguration config, Campaign cr) {
        //  Do we have an existing ID?  If not, generate one and write a new
        //  file.
Log.d(LOGBIT, "document directory: " + Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS));
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), config.getDataDirectory());
        if ((!dir.exists()) && (!dir.mkdirs())) {
            Log.e(LOGBIT, "Couldn't create directory");
throw new RuntimeException("couldn't create directory");
        }
        if (cr.getID() == null) {
            //  Look, don't start the save-game activity more than once per
            //  second, OK?
            cr.setID("save" + (cr.getTimestamp() / 1000L));
        }
        File file = new File(dir, cr.getID() + ".json");
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
        } catch (IOException ioe) {
Log.e(LOGBIT, "couldn't open file for output", ioe);
        }
        Gson gson = JSONGameRepository.newGsonBuilder().setPrettyPrinting().create();
//        JsonWriter writer = null;
//        try {
//            writer = gson.newJsonWriter(fw);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        gson.toJson(cr, fw);
        try {
            fw.close();
        } catch (IOException ioe) {
Log.e(LOGBIT, "couldn't close file after writing", ioe);
throw new RuntimeException("couldn't close file after writing", ioe);
        }
Log.i(LOGBIT, "WROTE FILE!!!");

        //  Now append to the index file.
//NOTE, we need to update the index file instead of appending to it when we're
//editing an existing record.
        file = new File(dir, CAMPAIGN_INDEX_FILE);
        boolean writeHeader = !file.exists();
        fw = null;
        try {
            fw = new FileWriter(file, true);
        } catch (IOException ioe) {
            Log.e(LOGBIT, "couldn't open index file for output", ioe);
        }
        try {
            if (writeHeader) {
                fw.append("//  Tab-separated file containing campaign-state-ID, name, mission, timestamp;\n")
                  .append("//  maybe a terrible idea.\n");
            }
            fw.append(cr.getID() + "\t" +
                    cr.getCampaignName().replaceAll("[\\t\\n]", " ") + "\t" +
                    cr.getMissionName().replaceAll("[\\t\\n]", " ") + "\t" +
                    cr.getTimestamp() + "\n");
            fw.close();
        } catch (IOException ioe) {
            Log.e(LOGBIT, "couldn't write to index file", ioe);
        }
    }
}
