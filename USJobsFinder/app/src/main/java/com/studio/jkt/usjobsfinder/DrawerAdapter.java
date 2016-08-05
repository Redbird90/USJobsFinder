package com.studio.jkt.usjobsfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by JDK on 6/6/2016.
 */
public class DrawerAdapter extends BaseAdapter {

    Context mainContext2;
    Activity mainActivity;
    private final String LOG_TAG = DrawerAdapter.class.getSimpleName();
    private int MY_PERMISSIONS_REQUEST_ACCESS_LOCATIONS = 1001;
    private Location drawerCurrLocation;
    private int PICK_LOCATION_REQUEST = 1002;
    private ToggleButton useSpecLocBtn;

    // filterTags specifies if tags filter is needed to be changed from default
    public boolean drawerFilterTags = false;
    public boolean drawerFilterFederalTag = true;
    public boolean drawerFilterStateTag = true;
    public boolean drawerFilterCountyTag = true;
    public boolean drawerFilterCityTag = true;
    public boolean drawerFilterPostDate = false;
    public String drawerFilterPostDateValue = "0";
    public boolean drawerFilterLocation = false;
    public boolean drawerFilterCurrLocation = false;
    public boolean drawerFilterSpecLocation = false;
    public boolean drawerSortingPrefsChanged = false;
    public int drawerSortingChoice = 0;
    public boolean drawerFilterPrefsChanged = false;
    public Location drawerSpecifiedLocation;

    public DrawerAdapter(Context context, Activity activity) {
        super();
        this.mainContext2 = context;
        this.mainActivity = activity;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v2 = convertView;
        if (v2 == null) {
            Log.i(LOG_TAG, "v is null");
            LayoutInflater inflater = (LayoutInflater) mainContext2.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v2 = inflater.inflate(R.layout.drawer_items, parent, false);
        } else {
            Log.i(LOG_TAG, "v is non-null");
        }

        RelativeLayout relativeLayout = (RelativeLayout) v2.findViewById(R.id.drawer_items_rel_lay);
        LinearLayout drawerLinLay1 = (LinearLayout) relativeLayout.findViewById(R.id.drawer_linearlayout_horizontal1);
        // Federal tag
        final ToggleButton fedTagBtn = (ToggleButton) drawerLinLay1.findViewById(R.id.drawer_tags_button1);
        // State tag
        final ToggleButton stateTagBtn = (ToggleButton) drawerLinLay1.findViewById(R.id.drawer_tags_button2);
        LinearLayout drawerLinLay2 = (LinearLayout) relativeLayout.findViewById(R.id.drawer_linearlayout_horizontal2);
        // County tag
        final ToggleButton countyTagBtn = (ToggleButton) drawerLinLay2.findViewById(R.id.drawer_tags_button3);
        // City tag
        final ToggleButton cityTagBtn = (ToggleButton) drawerLinLay2.findViewById(R.id.drawer_tags_button4);

        fedTagBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                drawerFilterFederalTag = isChecked;
                if (checkIfAllTagsFalse()) {
                    fedTagBtn.setChecked(true);
                    stateTagBtn.setChecked(true);
                    countyTagBtn.setChecked(true);
                    cityTagBtn.setChecked(true);
                    drawerFilterFederalTag = true;
                    drawerFilterStateTag = true;
                    drawerFilterCountyTag = true;
                    drawerFilterCityTag = true;
                }
                Log.i(LOG_TAG, "drawerFFTag is " + String.valueOf(drawerFilterFederalTag));
            }
        });

        stateTagBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                drawerFilterStateTag = isChecked;
                if (checkIfAllTagsFalse()) {
                    fedTagBtn.setChecked(true);
                    stateTagBtn.setChecked(true);
                    countyTagBtn.setChecked(true);
                    cityTagBtn.setChecked(true);
                    drawerFilterFederalTag = true;
                    drawerFilterStateTag = true;
                    drawerFilterCountyTag = true;
                    drawerFilterCityTag = true;
                }
            }
        });

        countyTagBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                drawerFilterCountyTag = isChecked;
                if (checkIfAllTagsFalse()) {
                    fedTagBtn.setChecked(true);
                    stateTagBtn.setChecked(true);
                    countyTagBtn.setChecked(true);
                    cityTagBtn.setChecked(true);
                    drawerFilterFederalTag = true;
                    drawerFilterStateTag = true;
                    drawerFilterCountyTag = true;
                    drawerFilterCityTag = true;
                }
            }
        });

        cityTagBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                drawerFilterCityTag = isChecked;
                if (checkIfAllTagsFalse()) {
                    fedTagBtn.setChecked(true);
                    stateTagBtn.setChecked(true);
                    countyTagBtn.setChecked(true);
                    cityTagBtn.setChecked(true);
                    drawerFilterFederalTag = true;
                    drawerFilterStateTag = true;
                    drawerFilterCountyTag = true;
                    drawerFilterCityTag = true;
                }
            }
        });

        NumberPicker postDateNumPicker = (NumberPicker) relativeLayout.findViewById(R.id.drawer_postdate_numpicker);
        postDateNumPicker.setMinValue(1);
        postDateNumPicker.setMaxValue(11);
        final String[] numPickerArray = new String[11];
        for (Integer i = 0; i < 101; i+=10) {
            numPickerArray[i/10] = i.toString();
        }

        postDateNumPicker.setDisplayedValues(numPickerArray);
        postDateNumPicker.setWrapSelectorWheel(true);

        postDateNumPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO: add textview with visibility changes upon 0 as newval/oldval
                Log.i(LOG_TAG, "postDateVal will be " + numPickerArray[picker.getValue()-1]);
                drawerFilterPostDateValue = numPickerArray[picker.getValue()-1];
            }
        });

        final ToggleButton useCurrLocBtn = (ToggleButton) relativeLayout.findViewById(R.id.drawer_currlocation_button);
        useSpecLocBtn = (ToggleButton) relativeLayout.findViewById(R.id.drawer_speclocation_button);
        useCurrLocBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                drawerFilterCurrLocation = isChecked;
                if (drawerFilterCurrLocation) {
                    Log.i(LOG_TAG, "useCurrLocBtn clicked and currLoc set to on");
                    drawerFilterLocation = true;
                    checkLocPermissionAndGet();
                    if (drawerFilterSpecLocation) {
                        Log.i(LOG_TAG, "drawerFilterSpecLoc true, changing bool and btn");
                        useSpecLocBtn.setChecked(false);
                        drawerFilterSpecLocation = false;
                    }
                } else {
                    Log.i(LOG_TAG, "useCurrLocBtn clicked and currLoc set to off");
                    if (!drawerFilterSpecLocation) {
                        drawerFilterLocation = false;
                    }
                }

            }
        });

        useSpecLocBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                drawerFilterSpecLocation = isChecked;
                if (drawerFilterSpecLocation) {
                    Log.i(LOG_TAG, "useSpecLocBtn clicked and loc picker opening...");
                    drawerFilterLocation = true;
                    Intent chooseLocIntent = new Intent(mainActivity, MapLocationActivity.class);
                    mainActivity.startActivityForResult(chooseLocIntent, PICK_LOCATION_REQUEST);
                    if (drawerFilterCurrLocation) {
                        Log.i(LOG_TAG, "useSpecLocBtn clicked, disabling drawerFilterCurrLoc bool and btn");
                        useCurrLocBtn.setChecked(false);
                        drawerFilterCurrLocation = false;
                    }
                } else {
                    Log.i(LOG_TAG, "useSpecLocBtn clicked and specLoc set to off");
                    if (!drawerFilterCurrLocation) {
                        drawerFilterLocation = false;
                    }
                }
            }
        });

        final RadioButton sortingRadBtn1 = (RadioButton) relativeLayout.findViewById(R.id.drawer_salaryascending_radiobtn1);
        final RadioButton sortingRadBtn2 = (RadioButton) relativeLayout.findViewById(R.id.drawer_salarydescending_radiobtn2);
        final RadioButton sortingRadBtn3 = (RadioButton) relativeLayout.findViewById(R.id.drawer_startdatenewest_radiobtn3);
        final RadioButton sortingRadBtn4 = (RadioButton) relativeLayout.findViewById(R.id.drawer_startdateoldest_radiobtn4);
        final RadioGroup sortingRadioGroup = (RadioGroup) relativeLayout.findViewById(R.id.drawer_sorting_radiogroup);
        sortingRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(LOG_TAG, "sorting option changed, Id is " + String.valueOf(checkedId));
                if (checkedId == sortingRadBtn1.getId()) {
                    drawerSortingChoice = 1;
                } else if (checkedId == sortingRadBtn2.getId()) {
                    drawerSortingChoice = 2;
                } else if (checkedId == sortingRadBtn3.getId()) {
                    drawerSortingChoice = 3;
                } else if (checkedId == sortingRadBtn4.getId()) {
                    drawerSortingChoice = 4;
                } else if (checkedId == -1) {
                    drawerSortingChoice = 0;
                }
            }
        });


        Log.i(LOG_TAG, "DrawerAdapter RUN, returning view");
        return v2;

    }

    @Override
    public int getViewTypeCount() {
        return 1;
    };

    @Override
    public int getItemViewType(int position) {
        return 1;
    }


    public Bundle getFiltersAndSortingData(Context context) {
        Bundle b = new Bundle();
        // Decide drawerFilterTags and drawerFilterLocation vars
        // cases for drawerFilterTags = false --> all tag vars true or all tag vars false
        this.drawerFilterTags = !(drawerFilterFederalTag && drawerFilterStateTag && drawerFilterCountyTag
                && drawerFilterCityTag || !drawerFilterFederalTag && !drawerFilterStateTag
                && !drawerFilterCountyTag && !drawerFilterCityTag);
        this.drawerFilterLocation = (drawerFilterCurrLocation || drawerFilterSpecLocation);
        Log.i(LOG_TAG, "getFiltersAndSortingData called, bools are " + String.valueOf(this.drawerFilterTags)
        + " and " + String.valueOf(this.drawerFilterLocation));

        b.putBoolean(context.getString(R.string.filterTagsKey), drawerFilterTags);
        b.putBoolean(context.getString(R.string.filterTagsFederalKey), drawerFilterFederalTag);
        b.putBoolean(context.getString(R.string.filterTagsStateKey), drawerFilterStateTag);
        b.putBoolean(context.getString(R.string.filterTagsCountyKey), drawerFilterCountyTag);
        b.putBoolean(context.getString(R.string.filterTagsCityKey), drawerFilterCityTag);

        //b.putBoolean(context.getString(R.string.filterPostDateKey), drawerFilterPostDate);
        b.putString(context.getString(R.string.filterPostDateValueKey), drawerFilterPostDateValue);

        b.putBoolean(context.getString(R.string.filterLocationKey), drawerFilterLocation);
        b.putBoolean(context.getString(R.string.filterCurrLocationKey), drawerFilterCurrLocation);
        b.putBoolean(context.getString(R.string.filterSpecLocationKey), drawerFilterSpecLocation);
        Log.i(LOG_TAG, "put filterSpecLoc, as " + String.valueOf(drawerFilterSpecLocation));
        //b.putBoolean(context.getString(R.string.sortingPrefsChangedKey), drawerSortingPrefsChanged);
        b.putInt(context.getString(R.string.sortChoiceKey), drawerSortingChoice);
        //b.putBoolean(context.getString(R.string.filterPrefsChangedKey), drawerFilterPrefsChanged);


        return b;

    }

    public void checkLocPermissionAndGet() {
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(mainContext2, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainContext2, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATIONS);
        } else {
            Log.i(LOG_TAG, "Location permissions granted in drawerAdapter");
            // Acquire a reference to the system Location Manager
            LocationManager locationManager2 = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            LocationListener locationListener2 = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.i(LOG_TAG, "onLocChanged in drawerAdapter, updating");
                    // Called when a new location is found by the network location provider.
                    if (location == null) {
                        Log.i(LOG_TAG, "location null in drawerAdapter, toast is notifying user");
                        drawerFilterCurrLocation = false;
                        if (!drawerFilterSpecLocation) {
                            drawerFilterLocation = false;
                        }
                        Toast toast = Toast.makeText(mainContext2, mainContext2.getString(R.string.location_permissions_denied), Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        drawerCurrLocation = new Location(location);
                        //drawerFilterLocation = true;
                        //drawerFilterCurrLocation = true;
                        Log.i(LOG_TAG, "onLocChanged in drawerAdapter, loc and vars updated");
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            //locationManager2.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
            locationManager2.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener2, Looper.getMainLooper());
            Log.i(LOG_TAG, "requestedLocUpdate, drawerCurrLoc is CURRENTLY " + String.valueOf(drawerCurrLocation));
        }
    }

    public boolean checkIfAllTagsFalse() {
/*        if (!drawerFilterFederalTag && !drawerFilterStateTag &&
                !drawerFilterCityTag && !drawerFilterCountyTag) {
            return true;
        } else {
            return false;
        }*/
        Log.i(LOG_TAG, "checking tags, complete bool val is " + String.valueOf(
                (!drawerFilterFederalTag && !drawerFilterStateTag &&
                        !drawerFilterCityTag && !drawerFilterCountyTag)));
        return (!drawerFilterFederalTag && !drawerFilterStateTag &&
                !drawerFilterCityTag && !drawerFilterCountyTag);
    }

    public void setDrawerFilterPrefsChanged(boolean drawerFilterPrefsChanged) {
        this.drawerFilterPrefsChanged = drawerFilterPrefsChanged;
    }

    public void setDrawerSortingPrefsChanged(boolean drawerSortingPrefsChanged) {
        this.drawerSortingPrefsChanged = drawerSortingPrefsChanged;
    }

    public void setDrawerCurrLocation(Location currLocation) {
        Log.i(LOG_TAG, "settingDrawerCurrLoc from drawerAdapter");
        if (currLocation == null) {
            Log.i(LOG_TAG, "currLocation null from drawerAdapter");
        }
        //drawerFilterLocation = true;
        drawerFilterCurrLocation = true;
        drawerFilterSpecLocation = false;
        drawerCurrLocation = new Location(currLocation);
    }

    public void setDrawerSpecifiedLocation(Location specLocation) {
        Log.i(LOG_TAG, "settingDrawerSpecLoc from mainAct");
        //drawerFilterLocation = true;
        drawerFilterSpecLocation = true;
        drawerSpecifiedLocation = new Location(specLocation);
    }

    public void resetSpecLocPref() {
        useSpecLocBtn.setChecked(false);
    }

    public boolean getDrawerFilterSpecLocation() {
        return drawerFilterSpecLocation;
    }

    public Location getUpdatedCurrLoc() {
        return drawerCurrLocation;
    }

    public Location getUpdatedSpecLoc() {
        return drawerSpecifiedLocation;
    }

}
