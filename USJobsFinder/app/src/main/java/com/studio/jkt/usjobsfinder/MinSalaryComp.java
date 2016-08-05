package com.studio.jkt.usjobsfinder;

import android.os.Bundle;
import android.util.Log;

import java.util.Comparator;

/**
 * Created by JDK on 6/6/2016.
 */
public class MinSalaryComp implements Comparator<Bundle> {

    final String JOB_MIN = "minimum";
    private static final String LOG_TAG = FilterPrefs.class.getSimpleName();

    @Override
    public int compare(Bundle lhs, Bundle rhs) {
        Log.i(LOG_TAG, "minSalaryComp, comparing " + String.valueOf(lhs.getInt(JOB_MIN))
                + " to " + String.valueOf(rhs.getInt(JOB_MIN)));
        if (lhs.getInt(JOB_MIN) > rhs.getInt(JOB_MIN)) {
            return 1;
        } else if (lhs.getInt(JOB_MIN) < rhs.getInt(JOB_MIN)) {
            return -1;
        }
        return 0;
    }
}
