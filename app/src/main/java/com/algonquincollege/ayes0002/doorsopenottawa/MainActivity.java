package com.algonquincollege.ayes0002.doorsopenottawa;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import com.algonquincollege.ayes0002.doorsopenottawa.model.Building;
import com.algonquincollege.ayes0002.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquincollege.ayes0002.doorsopenottawa.parsers.HttpMethod;
import com.algonquincollege.ayes0002.doorsopenottawa.parsers.RequestPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *  This application uses REST API server to fetch data from a JSON file in the web and display it
 *  in a list form. Once an element in the list is tapped it would navigate to another screen with
 *  the details of that particular building
 *  @author Hjalmar Ayestas (ayes0002@algonquinlive.com)
 */

public class MainActivity extends AppCompatActivity {

    // URL to Gerrys RESTful API Service hosted on his Bluemix account.
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String USERNAME = "ayes0002";
    public static final String PASSWORD = "password";

    private ProgressBar pb;
    private List<MyTask> tasks;
    private List<Building> buildingList;
    SwipeRefreshLayout mySwipeRefreshLayout;
    ListView list;
    FloatingActionButton fabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=(ListView)findViewById(R.id.list);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        fabButton = (FloatingActionButton) findViewById(R.id.fab);
        tasks = new ArrayList<>();
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);


        /*
        * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
        * performs a swipe-to-refresh gesture.
         */
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("log", "onRefresh called from SwipeRefreshLayout");
                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                mySwipeRefreshLayout.setRefreshing(false);
                updateDisplay();
            }
        });

        fabButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getApplicationContext(), AddBuildingActivity.class);
                startActivity(addIntent);
            }
        });

        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Building theSelectedBuilding = buildingList.get(position);
                Intent intent = new Intent( getApplicationContext(), DetailActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent.putExtra( "id", theSelectedBuilding.getBuildingId());
                intent.putExtra( "buildingName", theSelectedBuilding.getName() );
                intent.putExtra( "buildingAddress", theSelectedBuilding.getAddress() );
                intent.putExtra( "buildingDescription", theSelectedBuilding.getDescription());
                intent.putExtra( "buildingOpenHours", theSelectedBuilding.getDate());
                startActivity( intent );
                Toast.makeText(MainActivity.this, theSelectedBuilding.getName(), Toast.LENGTH_LONG).show();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Building theSelectedBuilding = buildingList.get(position);
                Intent editIntent = new Intent(getApplicationContext(), EditBuildingActivity.class);
                editIntent.putExtra( "id", theSelectedBuilding.getBuildingId());
                editIntent.putExtra( "name", theSelectedBuilding.getName());
                editIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(editIntent);
                return true;
            }
        });

        if (isOnline()) {
            requestData( REST_URI );
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show( getFragmentManager(), "About Dialog" );
            return true;
        }
//        else if (item.getItemId() == R.id.search){
//
//        }
        if ( item.isCheckable() ) {

            // which sort menu item did the user pick?
            switch (item.getItemId()) {
                case R.id.action_sort_name_asc:
                    Collections.sort(buildingList, new Comparator<Building>() {
                        @Override
                        public int compare(Building lhs, Building rhs) {
                            Log.i("Buildings", "Sorting buildings by name (a-z)");
                            return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                        }
                    });
                    break;

                case R.id.action_sort_name_dsc:
                    Collections.sort(buildingList, Collections.reverseOrder(new Comparator<Building>() {
                        @Override
                        public int compare(Building lhs, Building rhs) {
                            Log.i("Buildings", "Sorting buildings by name (z-a)");
                            return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                        }
                    }));
                    break;

//                case R.id.favourites:
//                    Collections.sort(buildingList, new Comparator<Building>() {
//                        @Override
//                        public int compare(Building lhs, Building rhs) {
//                            if(lhs > rhs){
//                                return 1;
//                            } else {
//                                return lhs.getName().compareTo( rhs.getName() );
//                            }
//                            return 0;
//                        }
//                    });
//                    break;
            }
        }
        updateDisplay();
        return false;
    }

    private void requestData(String uri) {
        getBuilding(uri);
    }

    protected void updateDisplay() {
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        list.setAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class MyTask extends AsyncTask<RequestPackage, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0], USERNAME, PASSWORD);
            buildingList = BuildingJSONParser.parseFeed(content);
            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = result;
            updateDisplay();
        }
    }

    private void createBuilding(String uri) {
        // Get the bundle of extras that was sent to this activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Building myBuilding = new Building();

            String buildingNameFromAddBuildingActivity = bundle.getString("buildingName");
            String buildingDescriptionFromAddBuildingActivity = bundle.getString("buildingDescription");
            String buildingAddressFromAddBuildingActivity = bundle.getString("buildingAddress");
            String buildingImageFromAddBuildingActivity = bundle.getString("buildingImage");

            myBuilding.setName(buildingNameFromAddBuildingActivity);
            myBuilding.setAddress(buildingAddressFromAddBuildingActivity);
            myBuilding.setImage(buildingImageFromAddBuildingActivity);
            myBuilding.setDescription(buildingDescriptionFromAddBuildingActivity);

            RequestPackage pkg = new RequestPackage();
            pkg.setMethod(HttpMethod.POST);
            pkg.setUri(uri);
            pkg.setParam("buildingId", myBuilding.getBuildingId() + "");
            pkg.setParam("name", myBuilding.getName());
            pkg.setParam("image", myBuilding.getImage());
            pkg.setParam("description", myBuilding.getDescription());

            MyTask postTask = new MyTask();
            postTask.execute((Runnable) pkg);
        }
    }

    private void getBuilding(String uri) {
        RequestPackage getPackage = new RequestPackage();
        getPackage.setMethod( HttpMethod.GET );
        getPackage.setUri( uri );
        GetTask getTask = new GetTask();
        getTask.execute( getPackage );
    }

    private class GetTask extends AsyncTask<RequestPackage, String, String> {


        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = HttpManager.getData(params[0], USERNAME ,PASSWORD);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = BuildingJSONParser.parseFeed(result);
            updateDisplay();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        requestData(IMAGES_BASE_URL + "/users/logout");
//    }
}
