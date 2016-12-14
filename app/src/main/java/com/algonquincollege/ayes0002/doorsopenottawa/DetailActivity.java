package com.algonquincollege.ayes0002.doorsopenottawa;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.algonquincollege.ayes0002.doorsopenottawa.MainActivity;
import com.algonquincollege.ayes0002.doorsopenottawa.model.Building;
import com.algonquincollege.ayes0002.doorsopenottawa.parsers.HttpMethod;
import com.algonquincollege.ayes0002.doorsopenottawa.parsers.RequestPackage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

/**
 * Created by hjalmarayestas on 2016-11-16.
 */

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView detailBuildingName;
    private TextView detailBuildingDescription;
    private TextView detailBuildingOpenHours;
    private Integer id;
    private GoogleMap mMap;
    private Geocoder mGeocoder;
    private String address;
//    private ProgressBar pb;

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
            id = bundle.getInt("id");
            String buildingNameFromMainActivity = bundle.getString("buildingName");
            String buildingDescriptionFromMainActivity = bundle.getString("buildingDescription");
            String buildingOpenHoursFromMainActivity = bundle.getString("buildingOpenHours");

            detailBuildingName.setText(buildingNameFromMainActivity);
            detailBuildingDescription.setText(buildingDescriptionFromMainActivity);
            detailBuildingOpenHours.setText(buildingOpenHoursFromMainActivity);
            address = bundle.getString("buildingAddress");

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // instantiate
//            mGeocoder = new Geocoder(this);
            pin(address);
            mGeocoder = new Geocoder( this, Locale.CANADA );

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_data) {
            if (isOnline()) {
                deleteBuilding( MainActivity.REST_URI + "/" + id );
                Intent addIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(addIntent);
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private void deleteBuilding(String uri) {
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.DELETE );
        pkg.setUri( uri );
        DoTask deleteTask = new DoTask();
        deleteTask.execute( pkg );
    }

    /**
     * Locate and pin locationName to the map.
     */
    private void pin(String locationName) {
        try {
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 12));
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

        pin(address);
    }

    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = HttpManager.getData(params[0], MainActivity.USERNAME, MainActivity.PASSWORD);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result == null) {
                Toast.makeText(DetailActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
