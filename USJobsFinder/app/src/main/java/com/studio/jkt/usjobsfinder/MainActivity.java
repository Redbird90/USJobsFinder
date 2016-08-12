package com.studio.jkt.usjobsfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private JobAdapter firstAdapter;
    private DrawerAdapter drawerAdapter;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mainToolbar;
    private EditText queryEditText;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Location currLocation;
    final private int MY_PERMISSIONS_REQUEST_ACCESS_LOCATIONS = 1001;
    final private int PICK_LOCATION_REQUEST = 1002;
    final private int SETTINGS_REQUEST = 1004;
    final private int LOAD_WEB_REQUEST = 1003;
    private FileIO fileIO;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener prefsListener;

    public boolean filterTags = false;
    public boolean filterFederalTag = true;
    public boolean filterStateTag = true;
    public boolean filterCountyTag = true;
    public boolean filterCityTag = true;
    public boolean filterPostDate = false;
    public String filterPostDateValue = "0";
    public Date userStartDateValue;
    public boolean filterLocation = false;
    public boolean filterCurrLocation = false;
    public boolean filterSpecLocation = false;
    public boolean sortingPrefsChanged = false;
    public int sortChoice = 0;
    public boolean filterPrefsChanged = false;
    public Location specifiedLocation;
    private boolean drawerClosed = true;
    private FilterPrefs favsDataFiltPrefs = new FilterPrefs();
    private Activity mainActRef;
    private String tsilSizeVal;
    private boolean openLinksInBrowser;

    // TODO: Institute null checks
    // TODO: Add textView right of numScrollWheel, call .setText("days")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "creating...4");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mainActRef = this;

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        tsilSizeVal = sharedPrefs.getString(getString(R.string.prefs_tsilsize_key), "50");
        openLinksInBrowser = sharedPrefs.getBoolean(getString(R.string.prefs_openlinks_key), false);

        prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences argSharedPrefs, String key) {
                Log.i(LOG_TAG, "onSharedPref starts call, key is " + key);
                if (key.equals(getString(R.string.prefs_tutorial_key))) {
                    Log.i(LOG_TAG, "tutorial pref changed");
                } else if (key.equals(getString(R.string.prefs_tsilsize_key))) {
                    tsilSizeVal = argSharedPrefs.getString(key, "50");
                    Log.i(LOG_TAG, "tsil size pref changed, " + tsilSizeVal);
                /*} else if (key.equals(getString(R.string.prefs_clearfavs_key))) {
                    Log.i(LOG_TAG, "clear favs pref changed, to " + argSharedPrefs.getBoolean(key, false));
                    if (argSharedPrefs.getBoolean(key, false)) {
                        if (favsDataFiltPrefs.clearFavoritesPermanently(fileIO)) {
                            Snackbar confirmClearedFavs = Snackbar.make(findViewById(R.id.root_linear_layout), getString(R.string.prefs_clearfavs_toastconfirmation), Snackbar.LENGTH_LONG);
                            confirmClearedFavs.show();
                            *//*Toast confirmClearedFavs = Toast.makeText(mainActRef, getString(R.string.prefs_clearfavs_toastconfirmation), Toast.LENGTH_LONG);
                            confirmClearedFavs.show();*//*
                        };
                        Log.i(LOG_TAG, "call sent to clear favs data from favsDataFiltPrefs");
                    }*/
                } else if (key.equals(getString(R.string.prefs_openlinks_key))) {
                    Log.i(LOG_TAG, "open links pref changed, to " + argSharedPrefs.getBoolean(key, false));
                }
            }
        };
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefsListener);


        //Log.i(LOG_TAG, "onCreate running" + String.valueOf(drawerAdapter.getDrawerFilterSpecLocation()));
        fileIO = new AndroidFileIO(this);
        mTitle = mDrawerTitle = getTitle();
        LinearLayout rootLinLayout = (LinearLayout) findViewById(R.id.root_linear_layout);

        mDrawerLayout = (DrawerLayout) rootLinLayout.findViewById(R.id.listact_drawer_lay);

        mainToolbar = (Toolbar) rootLinLayout.findViewById(R.id.toolbar_mainact);
        setSupportActionBar(mainToolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                Log.i(LOG_TAG, "drawer just closed, specLocBool is " + drawerAdapter.getDrawerFilterSpecLocation());
                super.onDrawerClosed(view);
                Bundle filtAndSortBundle = drawerAdapter.getFiltersAndSortingData(getApplicationContext());
                handleNewFiltAndSortPrefs(filtAndSortBundle);
                Log.i(LOG_TAG, "new prefs handled and drawer closed, filterPrefsChanged is " + String.valueOf(filterPrefsChanged)
                        + " and sortingPrefsChanged is " + String.valueOf(sortingPrefsChanged));
                if (filterPrefsChanged) {
                    searchJobs(queryEditText.getText().toString());
                    drawerAdapter.setDrawerFilterPrefsChanged(false);
                    filterPrefsChanged = false;
                    sortingPrefsChanged = false;
                } else if (sortingPrefsChanged) {
                    sortAdapter();
                    drawerAdapter.setDrawerSortingPrefsChanged(false);
                    sortingPrefsChanged = false;
                }
                drawerClosed = true;
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerClosed = false;
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                Utils.hideKeyboard(MainActivity.this);
            }

        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        RelativeLayout listRelLay = (RelativeLayout) mDrawerLayout.findViewById(R.id.jobstsil_rel_lay);
        queryEditText = (EditText) listRelLay.findViewById(R.id.query_edittext);
        queryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = queryEditText.getText().toString();
                    searchJobs(query);
                    Utils.hideKeyboard(mainActRef);
                    handled = true;
                }
                return handled;
            }
        });

        RecyclerView resultsRecView = (RecyclerView) listRelLay.findViewById(R.id.recyclerview_search_results);
        resultsRecView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        Log.i(LOG_TAG, "recycler at main act layout auto measuring set to " + String.valueOf(mLayoutManager.isAutoMeasureEnabled()));
        resultsRecView.setLayoutManager(mLayoutManager);

        drawerAdapter = new DrawerAdapter(getApplicationContext(), this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Ensure save data persists upon multiple app launches, and is only cleared in settings
            Log.i(LOG_TAG, "saving and loading favsDataFiltPrefs data");
            favsDataFiltPrefs.save(fileIO);
            favsDataFiltPrefs.load(fileIO);
        }

        String queryStr = "nursing+jobs+with+veterans+affairs";//"nursing+jobs+with+veterans+affairs+in+albany+ny";
        queryEditText.setText(queryStr);
        searchJobs(queryStr);
        populateRecyclerViewWithResults();

        ListView drawerLV = (ListView) mDrawerLayout.findViewById(R.id.nav_drawer_LV);
        drawerLV.setAdapter(drawerAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

/*        firstAdapter = new ArrayAdapter<Bundle>(getApplicationContext(), R.layout
                .drawer_items, R.id.list_item_job_textview, new ArrayList<Bundle>());*/

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "starting...");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.i(LOG_TAG, "restarting...");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "pausing...");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "resuming...");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.i(LOG_TAG, "stopping...");
        super.onStop();
    }

/*    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("MyBoolean", true);
        savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("MyString", "Welcome back to Android");
        // etc.
    }*/


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                goToFavoritesTsil();
                return true;
            case R.id.menu_settings:
                Intent prefsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(prefsIntent, SETTINGS_REQUEST);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        LinearLayout rootLinLayout2 = (LinearLayout) findViewById(R.id.root_linear_layout);
        DrawerLayout drawerLay = (DrawerLayout) rootLinLayout2.findViewById(R.id.listact_drawer_lay);
        RelativeLayout relLayNavDrawer = (RelativeLayout) drawerLay.findViewById(R.id.nav_drawer_rel_lay);
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(relLayNavDrawer);
        Log.i(LOG_TAG, "onPrepOptionsMenu run, drawerOpen is " + String.valueOf(drawerOpen));
        //TODO: update line below once menu is created
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i(LOG_TAG, "onReqPermResult, reqcode is " + String.valueOf(requestCode));
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "Location permissions granted in mainAct");
                    // Acquire a reference to the system Location Manager
/*                    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                    // Define a listener that responds to location updates
                    LocationListener locationListener = new LocationListener() {
                        public void onLocationChanged(Location location) {
                            Log.i(LOG_TAG, "onLocChanged in mainAct, updating");
                            // Called when a new location is found by the network location provider.
                            filterLocation = true;
                            filterCurrLocation = true;
                            filterSpecLocation = false;
                            if (location == null) {
                                Log.i(LOG_TAG, "currLoc null on onLocChanged mainAct");
                            }
                            currLocation = new Location(location);
                            drawerAdapter.setDrawerCurrLocation(currLocation);
                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onProviderDisabled(String provider) {
                        }
                    };
                    //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Log.i(LOG_TAG, "recheckedPermission, requestingSingleLocUpdate...");
                        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, Looper.getMainLooper());
                    }*/
                    drawerAdapter.checkLocPermissionAndGet();

                } else {
                    Log.i(LOG_TAG, "Location permissions NOT GRANTED in mainAct, get a toast going");
                    Snackbar snackbar2 = Snackbar.make(findViewById(R.id.root_linear_layout),
                            getString(R.string.location_error)+getString(R.string.location_permissions_denied), Snackbar.LENGTH_SHORT);
                    snackbar2.show();
                    /*Toast toast = Toast.makeText(this, getString(R.string.location_permissions_denied), Toast.LENGTH_SHORT);
                    toast.show();*/
                }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "onActResult, reqCode is " + String.valueOf(requestCode));
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == PICK_LOCATION_REQUEST) {
            // Make sure the request was successful
            Log.i(LOG_TAG, "onActResult for spec location picker");
            if (resultCode == RESULT_OK) {
                Double tempLat2 = data.getDoubleExtra("lat", 0.0);
                Double tempLong2 = data.getDoubleExtra("long", 0.0);
                Log.i(LOG_TAG, "onActResult for loc, lat is " + String.valueOf(tempLat2) + "and long is " + String.valueOf(tempLong2));
                if (tempLat2 != 0.0 && tempLong2 != 0.0) {
                /*if (specifiedLocation == null) {
                    specifiedLocation = new Location("nomatter");
                }*/
                    Location tempSpecLoc = new Location("nomatter");
                    tempSpecLoc.setLatitude(tempLat2);
                    tempSpecLoc.setLongitude(tempLong2);
                    //filterLocation = true;
                    //filterSpecLocation = true;
                    //filterCurrLocation = false;
                    drawerAdapter.setDrawerSpecifiedLocation(tempSpecLoc);
                    Log.i(LOG_TAG, "onActResult DRAWERfilterSpecLoc set to true, or " + String.valueOf(drawerAdapter.getDrawerFilterSpecLocation()));
                } else {
                    Log.i(LOG_TAG, "data consists of 0,0 lat and long");
                    drawerAdapter.resetSpecLocPref();
                    Snackbar snackbar3 = Snackbar.make(findViewById(R.id.root_linear_layout),
                            getString(R.string.location_error)+getString(R.string.location_connection_error), Snackbar.LENGTH_SHORT);
                    snackbar3.show();
                    /*Toast toast2 = Toast.makeText(this, getString(R.string.location_permissions_denied), Toast.LENGTH_SHORT);
                    toast2.show();*/
                }
                // Do something with the contact here (bigger example below)
            } else {
                Log.i(LOG_TAG, "result NOT ok after spec loc picker");
                drawerAdapter.resetSpecLocPref();
                Snackbar snackbar4 = Snackbar.make(findViewById(R.id.root_linear_layout), getString(R.string.location_not_chosen), Snackbar.LENGTH_SHORT);
                snackbar4.show();
                /*Toast toast = Toast.makeText(this, this.getString(R.string.location_not_chosen), Toast.LENGTH_SHORT);
                toast.show();*/
            }
        } else if (requestCode == LOAD_WEB_REQUEST) {
            Log.i(LOG_TAG, "onActResult webAct request found");
            if (resultCode == RESULT_OK) {
                // The user pressed favorites button in webAct
                Log.i(LOG_TAG, "onActRes webAct request is OK");
                Bundle soonToBeFavJob = data.getBundleExtra(getString(R.string.extra_webview_jobdata_key));
                boolean saveResult2 = saveJobBundle(soonToBeFavJob);
                Snackbar snackbar5;
                if (saveResult2) {
                    snackbar5 = Snackbar.make(findViewById(R.id.root_linear_layout), getString(R.string.favs_jobsaved), Snackbar.LENGTH_SHORT);
                } else {
                    snackbar5 = Snackbar.make(findViewById(R.id.root_linear_layout), getString(R.string.favs_jobremoved), Snackbar.LENGTH_SHORT);
                }
                snackbar5.show();
            }
        }
    }

    private void searchJobs(String queryStr) {
        RelativeLayout rootViewProg = (RelativeLayout) findViewById(R.id.jobstsil_rel_lay);
        ProgressBar progressBarMain = (ProgressBar) rootViewProg.findViewById(R.id.progressBarMainAct);
        progressBarMain.setVisibility(View.VISIBLE);
        FetchJobsTask jobsTask = new FetchJobsTask();
        //String queryStr = "nursing+jobs+with+veterans+affairs";//"nursing+jobs+with+veterans+affairs+in+albany+ny";
        jobsTask.execute(queryStr);
    }

    private void populateRecyclerViewWithResults() {
        firstAdapter = new JobAdapter(getApplicationContext(), this);
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.jobstsil_rel_lay);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_search_results);

        if (recyclerView == null) {
            Log.i(LOG_TAG, "LV is NULL");
        }
        if (firstAdapter == null) {
            Log.i(LOG_TAG, "FA is NULL");
        }

        recyclerView.setAdapter(firstAdapter);

    }

    private void handleNewFiltAndSortPrefs(Bundle prefsBundle) {
        Log.i(LOG_TAG, "mainAct is handling new filtnsort prefs, filterprefschanged is " + String.valueOf(filterPrefsChanged));
        this.filterTags = prefsBundle.getBoolean(getString(R.string.filterTagsKey));

        boolean tempFilterFederalTag = prefsBundle.getBoolean(getString(R.string.filterTagsFederalKey));
        boolean tempFilterStateTag = prefsBundle.getBoolean(getString(R.string.filterTagsStateKey));
        boolean tempFilterCountyTag = prefsBundle.getBoolean(getString(R.string.filterTagsCountyKey));
        boolean tempFilterCityTag = prefsBundle.getBoolean(getString(R.string.filterTagsCityKey));
        if (this.filterFederalTag != tempFilterFederalTag) {
            this.filterPrefsChanged = true;
        }
        if (this.filterStateTag != tempFilterStateTag) {
            this.filterPrefsChanged = true;
        }
        if (this.filterCountyTag != tempFilterCountyTag) {
            this.filterPrefsChanged = true;
        }
        if (this.filterCityTag != tempFilterCityTag) {
            this.filterPrefsChanged = true;
        }
        this.filterFederalTag = tempFilterFederalTag;
        this.filterStateTag = tempFilterStateTag;
        this.filterCountyTag = tempFilterCountyTag;
        this.filterCityTag = tempFilterCityTag;

        //this.filterPostDate = prefsBundle.getBoolean(getString(R.string.filterPostDateKey));
        String tempFilterPostDateValue = prefsBundle.getString(getString(R.string.filterPostDateValueKey));
        Log.i(LOG_TAG, "handling filter prefs, temp is " + tempFilterPostDateValue + ", curr is " + filterPostDateValue);
        if (this.filterPostDateValue != tempFilterPostDateValue) {
            this.filterPrefsChanged = true;
        } // TODO: Add else if postDate changed!
        this.filterPostDateValue = tempFilterPostDateValue;

        this.filterLocation = prefsBundle.getBoolean(getString(R.string.filterLocationKey));
        boolean tempFilterCurrLocation = prefsBundle.getBoolean(getString(R.string.filterCurrLocationKey));
        boolean tempFilterSpecLocation = prefsBundle.getBoolean(getString(R.string.filterSpecLocationKey));
        if (this.filterCurrLocation != tempFilterCurrLocation) {
            this.filterPrefsChanged = true;
        }
        Log.i(LOG_TAG, "got filterSpecsVars, filterSpec is " + String.valueOf(filterSpecLocation)
        + " and temp is " + String.valueOf(tempFilterSpecLocation));
        if (this.filterSpecLocation != tempFilterSpecLocation) {
            this.filterPrefsChanged = true;
        }
        this.filterCurrLocation = tempFilterCurrLocation;
        this.filterSpecLocation = tempFilterSpecLocation;

        // Locations not handled by Bundle arg, need to use getter methods
        // CHECK FOR NULL, LOCATION MAY NOT YET BE AVAILABLE
        if (this.filterCurrLocation) {
            Location tempCurrLocation;
            try {
                tempCurrLocation = new Location(drawerAdapter.getUpdatedCurrLoc());
            } catch (NullPointerException e) {
                // TODO: Change below toast to indicate location is being gathered still
                /*Snackbar snackbar = Snackbar.make(findViewById(R.id.root_linear_layout), R.string.location_permissions_denied, Snackbar.LENGTH_SHORT);
                snackbar.show();*/
                /*Toast toast3 = Toast.makeText(this, getString(R.string.location_permissions_denied), Toast.LENGTH_SHORT);
                toast3.show();*/
                tempCurrLocation = new Location("nomatter2");
            }
            try {
                // TODO: FIX or REMOVE below comparison as it is ridiculous and unnecessary
                Log.i(LOG_TAG, "location comparing1: " + Double.toString(tempCurrLocation.getLatitude()) + "," + Double.toString(tempCurrLocation.getLongitude())
                        + " TO " + Double.toString(currLocation.getLatitude()) + "," + Double.toString(currLocation.getLongitude()));
                if (Double.toString(tempCurrLocation.getLatitude()) + "," + Double.toString(tempCurrLocation.getLongitude())
                        != Double.toString(currLocation.getLatitude()) + "," + Double.toString(currLocation.getLongitude())) {
                    this.filterPrefsChanged = true;
                }
            } catch (NullPointerException e) {
                this.filterPrefsChanged = true;
            }
            this.currLocation = new Location(tempCurrLocation);
        }

        if (this.filterSpecLocation) {
            Location tempSpecifiedLocation = drawerAdapter.getUpdatedSpecLoc();
            try {
                Log.i(LOG_TAG, "location comparing2: " + Double.toString(tempSpecifiedLocation.getLatitude()) + "," + Double.toString(tempSpecifiedLocation.getLongitude())
                        + " TO " + Double.toString(specifiedLocation.getLatitude()) + "," + Double.toString(specifiedLocation.getLongitude()));
                if (Double.toString(tempSpecifiedLocation.getLatitude()) + "," + Double.toString(tempSpecifiedLocation.getLongitude())
                        != Double.toString(specifiedLocation.getLatitude()) + "," + Double.toString(specifiedLocation.getLongitude())) {
                    this.filterPrefsChanged = true;
                }
            } catch (NullPointerException e2) {
                this.filterPrefsChanged = true;
            }
            this.specifiedLocation = tempSpecifiedLocation;
        }

        //this.sortingPrefsChanged = prefsBundle.getBoolean(getString(R.string.sortingPrefsChangedKey));
        int tempSortChoice = prefsBundle.getInt(getString(R.string.sortChoiceKey));
        Log.i(LOG_TAG, "handling new prefs, sort choice was " + String.valueOf(this.sortChoice)
                + " and is now " + String.valueOf(tempSortChoice));
        if (this.sortChoice != tempSortChoice) {
            this.sortingPrefsChanged = true;
            Log.i(LOG_TAG, "sortingPrefsChanged set to true");
        }
        this.sortChoice = tempSortChoice;
        //this.filterPrefsChanged = prefsBundle.getBoolean(getString(R.string.filterPrefsChangedKey));
    }

    public class FetchJobsTask extends AsyncTask<String, Void, Bundle[]> {

        private final String LOG_TAG = FetchJobsTask.class.getSimpleName();

        @Override
        protected Bundle[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String queryString = params[0];
            Log.i(LOG_TAG, "QUERY AS " + queryString);

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jobsJsonStr = null;
            String ampers = "&";
            String orgIDStr = "";
            String highlightStr = "";
            //String sizeStr = "100";
            String fromStr = "";
            String tagsStr = "federal,state,county,city";
            String locStr = "";//"37.783333,-122.416667";

            List<String> tagsTsil = new ArrayList<String>();
            if (filterTags) {
                if (filterFederalTag) {
                    tagsTsil.add(getString(R.string.tag_value_federal));
                }
                if (filterStateTag) {
                    tagsTsil.add(getString(R.string.tag_value_state));
                }
                if (filterCountyTag) {
                    tagsTsil.add(getString(R.string.tag_value_county));
                }
                if (filterCityTag) {
                    tagsTsil.add(getString(R.string.tag_value_city));
                }
                tagsStr = TextUtils.join(",", tagsTsil);
                Log.i(LOG_TAG, "filterTags is TRUE and tagsStr is now " + tagsStr);
            }

            StringBuilder builder2 = new StringBuilder();
            builder2.append("https://api.usa.gov/jobs/search.json?");
            builder2.append("query=" + queryString + ampers);
            builder2.append("organization_ids=" + orgIDStr + ampers);
            builder2.append("hl=" + highlightStr + ampers);
            String actualSizeVal = "20";
            if (tsilSizeVal.equals("1")) {
                actualSizeVal = "20";
            } else if (tsilSizeVal.equals("2")) {
                actualSizeVal = "50";
            } else if (tsilSizeVal.equals("3")) {
                actualSizeVal = "75";
            } else if (tsilSizeVal.equals("4")) {
                actualSizeVal = "100";
            }
            builder2.append("size=" + actualSizeVal + ampers);
            if (!filterPostDateValue.equals("0")) {
                fromStr = filterPostDateValue;
                builder2.append("from=" + fromStr + ampers);
                Log.i(LOG_TAG, "searchingjobs, filterPostDateVal not 0 and from=" + fromStr);
            }
            builder2.append("tags=" + tagsStr + ampers);
            Log.i(LOG_TAG, "filterLoc, filterCurrLoc is " + String.valueOf(filterLocation) + String.valueOf(filterCurrLocation));
            if (filterLocation) {
                if (filterCurrLocation) {
                    locStr += Double.toString(currLocation.getLatitude()) + "," + Double.toString(currLocation.getLongitude());
                    Log.i(LOG_TAG, "filterCurrLoc true and locStr is " + locStr);
                    builder2.append("lat_lon=" + locStr);
                } else if (filterSpecLocation) {
                    locStr += Double.toString(specifiedLocation.getLatitude()) + "," + Double.toString(specifiedLocation.getLongitude());
                    Log.i(LOG_TAG, "filterSpecLoc true and locStr is " + locStr);
                    builder2.append("lat_lon=" + locStr);
                }
            } else {
                builder2.append("lat_lon=" + locStr);
            }
            String builtURL2 = builder2.toString();
            Log.i(LOG_TAG, "StringBuilt URI: " + builtURL2);


            //"http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7"
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(builtURL2);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jobsJsonStr = buffer.toString();

/*                Log.v(LOG_TAG, "Built URI: " + builtURLString);
                Log.v(LOG_TAG, "Forecast JSON String: " + jobsJsonStr);
                Log.v(LOG_TAG, "Built URI: " + builtURLString);*/

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            String[] parsedJSONData = new String[100];

            try {
                return getJobDataFromJson(jobsJsonStr);
            } catch (JSONException e) {
                Log.v(LOG_TAG, "JSON Exception while parsing weather data: " + e);
                return null;
            }

            //return null;
        }

        @Override
        protected void onPostExecute(Bundle[] bundles) {
            RelativeLayout rootView = (RelativeLayout) findViewById(R.id.jobstsil_rel_lay);
            if (bundles != null) {
                firstAdapter.clearJobs();
                for (Bundle jobBundle : bundles) {
                    firstAdapter.addJob(jobBundle);
                }
                RecyclerView recyclerView3 = (RecyclerView) rootView.findViewById(R.id.recyclerview_search_results);
                recyclerView3.setAdapter(firstAdapter);
            } else {
                // TODO: Show error prompt to user
                Snackbar searchErrorSnackbar = Snackbar.make(findViewById(R.id.root_linear_layout), getString(R.string.search_error_msg), Snackbar.LENGTH_LONG);
                searchErrorSnackbar.show();
                /*Toast searchErrorToast = Toast.makeText(mainActRef, getString(R.string.search_error_msg), Toast.LENGTH_LONG);
                searchErrorToast.show();*/
            }
            // Sorting occurs here, in onPostExecute!
            if (sortChoice != 0) {
                sortAdapter();
            }
            ProgressBar progressBarMain2 = (ProgressBar) rootView.findViewById(R.id.progressBarMainAct);
            progressBarMain2.setVisibility(View.INVISIBLE);
        }
    }


    private Bundle[] getJobDataFromJson(String jobsJsonStr)
            throws JSONException {

        final String LOG_TAG = MainActivity.class.getSimpleName();

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

        JSONArray jobsJson = new JSONArray(jobsJsonStr);
        int numJobs = jobsJson.length();
        Log.i(LOG_TAG, String.valueOf(numJobs));
        Log.i(LOG_TAG, jobsJson.toString());
        //Log.i(LOG_TAG, String.valueOf(jobsJsonStr));

        Bundle[] resultStrs = new Bundle[numJobs];
        for (int i = 0; i < jobsJson.length(); i++) {


            // Get the JSON object representing the day
            JSONObject specJob = jobsJson.getJSONObject(i);
            //Log.i(LOG_TAG, String.valueOf(specJob));
            Bundle bundle = new Bundle();

            String jobIDfromJSON = specJob.getString(JOB_ID);
            bundle.putString(JOB_ID, jobIDfromJSON);
            //Log.i(LOG_TAG, jobIDfromJSON);

            String jobTitlefromJSON = specJob.getString(JOB_TITLE);
            bundle.putString(JOB_TITLE, jobTitlefromJSON);

            String jobOrgNamefromJSON = specJob.getString(JOB_ORGNAME);
            bundle.putString(JOB_ORGNAME, jobOrgNamefromJSON);

            String jobCodefromJSON = specJob.getString(JOB_CODE);
            bundle.putString(JOB_CODE, jobCodefromJSON);

            //Log.i(LOG_TAG, "jobMinStr is " + String.valueOf(specJob.getInt(JOB_MIN)));

            try {
                int jobMinfromJSON = specJob.getInt(JOB_MIN);
                bundle.putInt(JOB_MIN, jobMinfromJSON);
            } catch (JSONException jsonE) {
                try {
                    int jobMinfromJSON = (int) (specJob.getDouble(JOB_MIN));
                    bundle.putInt(JOB_MIN, jobMinfromJSON);
                    Log.i(LOG_TAG, "JSONEXC caught, for " + String.valueOf(jobMinfromJSON));
                } catch (JSONException jsonDoubleError) {
                    String jobMinfromJSON = specJob.get(JOB_MIN).toString();
                        Log.i(LOG_TAG, "DOUBLEJSONEXC caught, " + String.valueOf(jobMinfromJSON));
                }
            }

            try {
                int jobMaxfromJSON = specJob.getInt(JOB_MAX);
                bundle.putInt(JOB_MAX, jobMaxfromJSON);
            } catch (JSONException jsonE2) {
                try {
                    int jobMaxfromJSON = (int) specJob.getDouble(JOB_MAX);
                    bundle.putInt(JOB_MAX, jobMaxfromJSON);
                    Log.i(LOG_TAG, "JSONEXC caught, for " + String.valueOf(jobMaxfromJSON));
                } catch (JSONException jsonDoubleError2) {
                    String jobMaxfromJSON = specJob.get(JOB_MAX).toString();
                    Log.i(LOG_TAG, "DOUBLEJSONEXC caught, " + String.valueOf(jobMaxfromJSON));
                }
            }

            String jobStartfromJSON = specJob.getString(JOB_START);
            bundle.putString(JOB_START, jobStartfromJSON);

            String jobEndfromJSON = specJob.getString(JOB_END);
            bundle.putString(JOB_END, jobEndfromJSON);

            JSONArray jobLocationsfromJSON = specJob.getJSONArray(JOB_LOC_array);
            String[] jobLocationsArray = new String[jobLocationsfromJSON.length()];
            for (int x = 0; x < jobLocationsfromJSON.length(); x++) {
                jobLocationsArray[x] = jobLocationsfromJSON.getString(x);
            }
            bundle.putStringArray(JOB_LOC_array, jobLocationsArray);

            String jobUrlfromJSON = specJob.getString(JOB_URL);
            bundle.putString(JOB_URL, jobUrlfromJSON);


            resultStrs[i] = bundle;
/*        for (String s : resultStrs) {
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }*/
        }

        return resultStrs;

    }

    private void goToFavoritesTsil() {

        Log.i(LOG_TAG, "going to Favorites now...");
        Bundle[] mainActFavsTsil = favsDataFiltPrefs.getFavorites();
        firstAdapter.clearJobs();
        for (Bundle jobBundle3 : mainActFavsTsil) {
            firstAdapter.addJob(jobBundle3);
        }

        RelativeLayout rootView2 = (RelativeLayout) findViewById(R.id.jobstsil_rel_lay);

        RecyclerView recyclerView4 = (RecyclerView) rootView2.findViewById(R.id.recyclerview_search_results);
        recyclerView4.setAdapter(firstAdapter);

    }

    public void checkDrawerCloseAndSearch() {
        if (this.drawerClosed) {
            Bundle filtAndSortBundle2 = drawerAdapter.getFiltersAndSortingData(getApplicationContext());
            handleNewFiltAndSortPrefs(filtAndSortBundle2);
            Log.i(LOG_TAG, "new prefs handled and drawer closed, filterPrefsChanged is " + String.valueOf(filterPrefsChanged)
                    + " and sortingPrefsChanged is " + String.valueOf(sortingPrefsChanged));
            if (filterPrefsChanged) {
                searchJobs(queryEditText.getText().toString());
                drawerAdapter.setDrawerFilterPrefsChanged(false);
                filterPrefsChanged = false;
                sortingPrefsChanged = false;
            } else if (sortingPrefsChanged) {
                sortAdapter();
                drawerAdapter.setDrawerSortingPrefsChanged(false);
                sortingPrefsChanged = false;
            }
        }
    }

    private void sortAdapter() {

        // TODO: Ensure null values are sorted appropriately and do not bug the sorter
        ArrayList<Bundle> tempTsil = firstAdapter.getTsil();


        if (this.sortChoice == 1) {

            Collections.sort(tempTsil, new MinSalaryComp());
            Log.i(LOG_TAG, "running sortAdapter, " + String.valueOf(this.sortChoice));

        } else if (this.sortChoice == 2) {

            Collections.sort(tempTsil, new MinSalaryComp());
            Collections.reverse(tempTsil);
            Log.i(LOG_TAG, "running sortAdapter, " + String.valueOf(this.sortChoice));

        } else if (this.sortChoice == 3) {

            Collections.sort(tempTsil, new StartDateComp());
            Collections.reverse(tempTsil);
            Log.i(LOG_TAG, "running sortAdapter, " + String.valueOf(this.sortChoice));

        } else if (this.sortChoice == 4) {

            Collections.sort(tempTsil, new StartDateComp());
            Log.i(LOG_TAG, "running sortAdapter, " + String.valueOf(this.sortChoice));

        }

        if (this.sortChoice == 0) {
            Log.i(LOG_TAG, "not running sortAdapter, " + String.valueOf(this.sortChoice));
        }

        firstAdapter.setJobTsil(tempTsil);

    }

    private boolean saveJobBundle(Bundle bundledJobData) {
        final String JOB_ID = "id";
        Log.i(LOG_TAG, "saving job, id is " + bundledJobData.get(JOB_ID));
        // TODO: Ensure error handling so user if notified of errors
        boolean favJobAddResult = favsDataFiltPrefs.addFavoriteJob(bundledJobData);
        favsDataFiltPrefs.save(fileIO);
        return favJobAddResult;
    }

}
