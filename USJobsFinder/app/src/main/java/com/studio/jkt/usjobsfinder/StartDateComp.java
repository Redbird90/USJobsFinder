package com.studio.jkt.usjobsfinder;

import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by JDK on 6/6/2016.
 */
public class StartDateComp implements Comparator<Bundle> {

    final String JOB_START = "start_date";
    final String LOG_TAG = StartDateComp.class.getSimpleName();

    @Override
    public int compare(Bundle lhs, Bundle rhs) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;

        try {
            date1 = format.parse(lhs.getString(JOB_START));
        } catch (ParseException e) {
            Log.i(LOG_TAG, "error1 while comparing " + lhs.getString(JOB_START));
            e.printStackTrace();
        }
        try {
            date2 = format.parse(rhs.getString(JOB_START));
        } catch (ParseException e) {
            Log.i(LOG_TAG, "error2 while comparing " + rhs.getString(JOB_START));
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "startDateComp, comparing " + date1.toString() + " to "
        + date2.toString());
        try {
            if (date1.after(date2)) {
                return 1;
            } else if (date1.before(date2)) {
                return -1;
            } else {
                return 0;
            }
        } catch (NullPointerException e) {
            if (date1 == null) {
                Log.i(LOG_TAG, "date1 is NULL");
            }
            if (date2 == null) {
                Log.i(LOG_TAG, "date2 is NULL");
            }
            e.printStackTrace();
        }
        return 0;
    }
}
