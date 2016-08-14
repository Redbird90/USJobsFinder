package com.studio.jkt.usjobsfinder;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by JDK on 6/13/2016.
 */
public class MapLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Double tempLat;
    private Double tempLong;
    private String LOG_TAG = MapLocationActivity.class.getSimpleName();
    Toolbar mapToolbar;
    LinearLayout linearLayoutRoot;
    //GoogleMap map;
    //MapFragment mapFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maplocation);

        linearLayoutRoot = (LinearLayout) findViewById(R.id.map_linearlayout_root);

        Log.i(LOG_TAG, "creating map fragment");
        MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_linearlayout_inner, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);

        mapToolbar = (Toolbar) linearLayoutRoot.findViewById(R.id.toolbar_mapact);
        setSupportActionBar(mapToolbar);

    }



    @Override
    public void onMapReady(final GoogleMap specLocMap) {
        LatLngBounds america = new LatLngBounds(new LatLng(40.99, -125.28), new LatLng(41.14, -66.23));
        Log.i(LOG_TAG, specLocMap.getCameraPosition().toString());

        specLocMap.animateCamera(CameraUpdateFactory.newLatLngBounds(america, 10), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.i(LOG_TAG, "animation finished");
            }

            @Override
            public void onCancel() {
                Log.i(LOG_TAG, "animation cancelled");
            }
        });

        final Marker[] specLocMarker = new Marker[1];

        specLocMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (specLocMarker[0] != null) {
                    specLocMarker[0].remove();
                }
                specLocMarker[0] = specLocMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.map_saved_specloc)));
                Snackbar mapSnack = Snackbar.make(linearLayoutRoot, getString(R.string.map_location_saved), Snackbar.LENGTH_SHORT);
                tempLat = latLng.latitude;
                tempLong = latLng.longitude;
                mapSnack.show();
                Intent actResultIntent = new Intent();
                actResultIntent.putExtra("lat", tempLat);
                actResultIntent.putExtra("long", tempLong);
                Log.i(LOG_TAG, "onMapLongClick, lat is " + String.valueOf(tempLat) + "and long is " + String.valueOf(tempLong));
                setResult(RESULT_OK, actResultIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i(LOG_TAG, "optionItemSelected...");
        switch (item.getItemId()) {
            case R.id.menuitem_exit:
                exitMapActivity();
                Log.i(LOG_TAG, "exit menu btn clicked");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG_TAG, "creating options menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cust_exitable_menu, menu);
        return true;


    }

    private void exitMapActivity() {
        finish();
        Log.i(LOG_TAG, "exiting mapAct...");
    }

    @Override
    public void onBackPressed() {
        Log.i(LOG_TAG, "back pressed in maplocact");
        super.onBackPressed();
    }
}
