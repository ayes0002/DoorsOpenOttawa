package com.algonquincollege.ayes0002.doorsopenottawa;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by hjalmarayestas on 2016-11-16.
 */

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView detailBuildingName;
    private TextView detailBuildingDescription;
    private TextView detailBuildingOpenHours;
    private GoogleMap mMap;
    private Geocoder mGeocoder;
    private EditText userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailBuildingName = (TextView) findViewById(R.id.buildingName);
        detailBuildingDescription = (TextView) findViewById(R.id.buildingDescription);
        detailBuildingOpenHours = (TextView) findViewById(R.id.buildingOpenHours);

        // Get the bundle of extras that was sent to this activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String buildingNameFromMainActivity = bundle.getString("buildingName");
            String buildingDescriptionFromMainActivity = bundle.getString("buildingDescription");
            String buildingOpenHoursFromMainActivity = bundle.getString("buildingOpenHours");

            detailBuildingName.setText(buildingNameFromMainActivity);
            detailBuildingDescription.setText(buildingDescriptionFromMainActivity);
            detailBuildingOpenHours.setText(buildingOpenHoursFromMainActivity);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // instantiate
            mGeocoder = new Geocoder(this);
            //mGeocoder = new Geocoder( this, locale.CANADA );

//            final EditText userLocation = (EditText) findViewById(R.id.userLocation);
//            userLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if(actionId == EditorInfo.IME_NULL && event.getAction( ) == KeyEvent.ACTION_DOWN
//                            && event.getKeyCode( ) == KeyEvent.KEYCODE_ENTER){
//                        DetailActivity.this.pin(userLocation.getText().toString());
//                        userLocation.setText("");
//                        return true;
//                    } else {
//                        return false;
//                    }
//                };
//            });

        }
    }

    /**
     * Locate and pin locationName to the map.
     */
    private void pin(String locationName) {
        try {
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // pin each of Algonquin College's campuses to the map
    }


}
