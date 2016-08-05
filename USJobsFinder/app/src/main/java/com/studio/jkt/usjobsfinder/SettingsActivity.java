package com.studio.jkt.usjobsfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

/**
 * Created by James on 7/13/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.sett_framelay, new SettingsFragment())
                .commit();

        Log.i(LOG_TAG, "setting exitable menu now...");
        LinearLayout settActRootLay = (LinearLayout) findViewById(R.id.sett_lin_lay);
        Toolbar settToolbar = (Toolbar) settActRootLay.findViewById(R.id.toolbar_settact);
        setSupportActionBar(settToolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i(LOG_TAG, "optionItemSelected...");
        switch (item.getItemId()) {
            case R.id.menuitem_exit:
                exitSettActivity();
                Log.i(LOG_TAG, "exit menu btn clicked");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cust_exitable_menu, menu);
        return true;
    }

    private void exitSettActivity() {
        finish();
        Log.i(LOG_TAG, "exiting settingsAct...");
    }
}
