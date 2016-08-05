package com.studio.jkt.usjobsfinder;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by James on 7/12/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    FilterPrefs favsDataFiltPrefs2 = new FilterPrefs();
    String LOG_TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load prefs from XML
        addPreferencesFromResource(R.xml.preferences);

        final Preference clearFavsPref = findPreference(getString(R.string.prefs_clearfavs_key));
        clearFavsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                preference.getEditor().putBoolean(preference.getKey(), true);
                Log.i(LOG_TAG, "clear favorites initiated");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.prefs_clearfavs_dialogtitle))
                        .setPositiveButton(getString(R.string.prefs_clearfavs_dialogposmsg), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(LOG_TAG, "user confirmed deletion of favorites tsil, updating pref");

                                SharedPreferences.Editor clearFavsEditor = clearFavsPref.getEditor();
                                clearFavsEditor.putBoolean(getString(R.string.prefs_clearfavs_key), true);
                                clearFavsEditor.apply();
                                FileIO fileIO2 = new AndroidFileIO(getActivity());
                                if (favsDataFiltPrefs2.clearFavoritesPermanently(fileIO2)) {
                                    Toast confirmClearedFavs = Toast.makeText(getActivity(), getString(R.string.prefs_clearfavs_toastconfirmation), Toast.LENGTH_LONG);
                                    confirmClearedFavs.show();
                                }
                                Log.i(LOG_TAG, "clear favs pref updated to true and applied");
                            }
                        })
                        .setNegativeButton(getString(R.string.prefs_clearfavs_dialognegmsg), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Naught is done here, dialog dismisses automatically
                            }
                        });
                builder.show();
                return true;
            }
        });
    }

}
