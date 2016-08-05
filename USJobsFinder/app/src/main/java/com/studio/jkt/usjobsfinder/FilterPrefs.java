package com.studio.jkt.usjobsfinder;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by JDK on 6/3/2016.
 */
public class FilterPrefs {

    // Create variables that will hold the values you want to save
    // Default values:
    //TODO:CHECK W2W Settings for correct total height scaled saving

    public static int numOfFavJobs = 0;
    public static Bundle[] favsJobData;
    private static final String LOG_TAG = FilterPrefs.class.getSimpleName();


    // save method is used to write updated sound setting and score value(key, value) to a
    // .savedata file
    public static void save(FileIO files) {
        BufferedWriter out = null;
        // TODO: add check for whether jobs data is null (perhaps never loaded)
        if (favsJobData == null) {
            Log.i(LOG_TAG, "favsJobData found as NULL in save()");
            favsJobData = new Bundle[numOfFavJobs];
        }
        // These are the names of the JSON objects that need to be extracted.
        final String JOB_ID = "id";
        final String JOB_TITLE = "position_title";
        final String JOB_ORGNAME = "organization_name";
        final String JOB_CODE = "rate_interval_code";
        final String JOB_MIN = "minimum";
        final String JOB_MAX = "maximum";
        final String JOB_START = "start_date";
        final String JOB_END = "end_date";
        final String JOB_LOC_array = "locations";
        final String JOB_URL = "url";

        Log.i(LOG_TAG, "attempting data save...");
        try {

            // Writes a file called .usjobsfavs to the SD Card
            out = new BufferedWriter(new OutputStreamWriter(
                    files.writeFile(".usjobsfavs")));

            // Line by line ("\n" creates a new line), write the value of each variable to the file.
            // Writes number of jobs value to file
            out.write(String.valueOf(numOfFavJobs));
            out.write("\n");
            Log.i(LOG_TAG, "wrote " + String.valueOf(numOfFavJobs) + " as numoffavjobs");

            // Loops through numOfFavJobs times to write each job's data
            for (int x = 0; x < numOfFavJobs; x++) {
                out.write(favsJobData[x].getString(JOB_ID));
                out.write("\n");
                out.write(favsJobData[x].getString(JOB_TITLE));
                out.write("\n");
                out.write(favsJobData[x].getString(JOB_ORGNAME));
                out.write("\n");
                out.write(favsJobData[x].getString(JOB_CODE));
                out.write("\n");
                out.write(String.valueOf(favsJobData[x].getInt(JOB_MIN)));
                out.write("\n");
                out.write(String.valueOf(favsJobData[x].getInt(JOB_MAX)));
                out.write("\n");
                out.write(favsJobData[x].getString(JOB_START));
                out.write("\n");
                out.write(favsJobData[x].getString(JOB_END));
                out.write("\n");
                String[] tempJobLocArray = favsJobData[x].getStringArray(JOB_LOC_array);
                out.write(String.valueOf(tempJobLocArray.length));
                out.write("\n");
                for (int y = 0; y < tempJobLocArray.length; y++) {
                    out.write(tempJobLocArray[y]);
                    out.write("\n");
                }
                out.write(favsJobData[x].getString(JOB_URL));
                out.write("\n");
            }

            // This section handles errors in file management!

        } catch (IOException e) {
            Log.i(LOG_TAG, "caught IOException1b " + e.toString());
        } finally {
            try {
                if (out != null)
                    out.close();
                    Log.i(LOG_TAG, "file closed, data saved");
            } catch (IOException e) {
                Log.i(LOG_TAG, "caught IOException2b " + e.toString());
            }
        }
    }

    // load method is used to read sound setting and score value from .savedata file
    public static void load(FileIO files) {
        BufferedReader in = null;
        final String JOB_ID = "id";
        final String JOB_TITLE = "position_title";
        final String JOB_ORGNAME = "organization_name";
        final String JOB_CODE = "rate_interval_code";
        final String JOB_MIN = "minimum";
        final String JOB_MAX = "maximum";
        final String JOB_START = "start_date";
        final String JOB_END = "end_date";
        final String JOB_LOC_array = "locations";
        final String JOB_URL = "url";

        Log.i(LOG_TAG, "attempting data load...");
        try {
            // Reads file called Save Data
            in = new BufferedReader(new InputStreamReader(
                    files.readFile(".usjobsfavs")));
            Log.i(LOG_TAG, String.valueOf(in == null) + " null check for BuffReader, parsing...");

            // Loads values from the file and replaces default values.
            // Loads number of jobs value from file
            String tempNumStr = in.readLine();
            Log.i(LOG_TAG, "tempNum is " + tempNumStr);
            numOfFavJobs = Integer.parseInt(tempNumStr);
            Log.i(LOG_TAG, "numOfFavs is " + String.valueOf(numOfFavJobs));
            favsJobData = new Bundle[numOfFavJobs];

            // Loop through numOfFavJobs times to read each job's data
            for (int i = 0; i < numOfFavJobs; i++) {
                Bundle bundleJobFav = new Bundle();
                bundleJobFav.putString(JOB_ID, in.readLine());
                bundleJobFav.putString(JOB_TITLE, in.readLine());
                bundleJobFav.putString(JOB_ORGNAME, in.readLine());
                bundleJobFav.putString(JOB_CODE, in.readLine());
                bundleJobFav.putInt(JOB_MIN, Integer.parseInt(in.readLine()));
                bundleJobFav.putInt(JOB_MAX, Integer.parseInt(in.readLine()));
                bundleJobFav.putString(JOB_START, in.readLine());
                bundleJobFav.putString(JOB_END, in.readLine());
                int tempLengthInt = Integer.parseInt(in.readLine());
                String[] tempStrArray = new String[tempLengthInt];
                for (int z = 0; z < tempLengthInt; z++) {
                    tempStrArray[z] = in.readLine();
                }
                bundleJobFav.putStringArray(JOB_LOC_array, tempStrArray);
                bundleJobFav.putString(JOB_URL, in.readLine());
                favsJobData[i] = bundleJobFav;
            }

        } catch (IOException e) {
            Log.i(LOG_TAG, "caught IOException1a " + e.toString());
            // Catches errors. Default values are used.
        } catch (NumberFormatException e) {
            Log.i(LOG_TAG, "caught NumFormException " + e.toString());
            // Catches errors. Default values are used.
        } finally {
            try {
                if (in != null)
                    Log.i(LOG_TAG, "file closed, data loaded");
                    in.close();
            } catch (IOException e) {
                Log.i(LOG_TAG, "caught IOException2a " + e.toString());
            }
        }
    }

    public boolean addFavoriteJob(Bundle newJob) {

        final String JOB_ID = "id";
        final String JOB_TITLE3 = "position_title";

        Log.i(LOG_TAG, "adding Fav, id is " + newJob.getString(JOB_ID));
        boolean removeFavorite = false;
        int removeNum = 0;
        boolean jobRemoved = false;

        for (int b = 0; b < numOfFavJobs; b++) {
            if (favsJobData[b].getString(JOB_ID) == newJob.getString(JOB_ID)) {
                Log.i(LOG_TAG, "found duplicate favorite, ids are " + favsJobData[b].getString(JOB_ID)
                + " , " + newJob.getString(JOB_ID) + " and titles are " + favsJobData[b].getString(JOB_TITLE3)
                + " , " + newJob.getString(JOB_TITLE3));
                removeFavorite = true;
                removeNum = b;
                jobRemoved = false;
            }
        }
        if (removeFavorite) {
            Log.i(LOG_TAG, "attempting favorite removal...");
            Bundle[] tempBundleArray2 = new Bundle[numOfFavJobs-1];
            for (int c = 0; c < tempBundleArray2.length; c++) {
                if (c == removeNum) {
                    jobRemoved = true;
                }
                if (jobRemoved) {
                    tempBundleArray2[c] = favsJobData[c+1];
                } else {
                    tempBundleArray2[c] = favsJobData[c];
                }
            }
            favsJobData = tempBundleArray2;
            numOfFavJobs -= 1;
            return false;

        } else {

            Log.i(LOG_TAG, "attempting fav data storage...");
            Bundle[] tempBundleArray = new Bundle[numOfFavJobs + 1];
            for (int a = 0; a < numOfFavJobs; a++) {
                tempBundleArray[a] = favsJobData[a];
            }
            tempBundleArray[numOfFavJobs] = newJob;
            favsJobData = tempBundleArray;
            numOfFavJobs += 1;
            return true;
        }
    }

    public boolean clearFavoritesPermanently(FileIO files2) {
        numOfFavJobs = 0;
        favsJobData = new Bundle[numOfFavJobs];
        save(files2);
        return true;
    }

    public Bundle[] getFavorites() {
        Bundle[] tempTsil = favsJobData;
        return tempTsil;
    }
}
