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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.ayes0002.doorsopenottawa.model.Building;
import com.algonquincollege.ayes0002.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 *  This application uses REST API server to fetch data from a JSON file in the web and display it
 *  in a list form. Once an element in the list is tapped it would navigate to another screen with
 *  the details of that particular building
 *  @author Hjalmar Ayestas (ayes0002@algonquinlive.com) Anton Antonenko (anto@algonquinlive.com)
 */

public class MainActivity extends ListActivity { //implements AdapterView.OnItemClickListener {

    // URL to Gerrys RESTful API Service hosted on his Bluemix account.
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private ProgressBar pb;
    private List<MyTask> tasks;
    private List<Building> buildingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        getListView().setOnItemClickListener(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Building theSelectedBuilding = buildingList.get(position);
                Intent intent = new Intent( getApplicationContext(), DetailActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent.putExtra( "buildingName", theSelectedBuilding.getName() );
                intent.putExtra( "buildingAddress", theSelectedBuilding.getAddress() );
                intent.putExtra( "buildingDescription", theSelectedBuilding.getDescription());
                intent.putExtra( "buildingOpenHours", theSelectedBuilding.getDate());
                startActivity( intent );
                Toast.makeText(MainActivity.this, theSelectedBuilding.getName(), Toast.LENGTH_LONG).show();
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
        return false;
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Building theSelectedBuilding = buildingList.get(position);
//        Intent intent = new Intent( getApplicationContext(), DetailActivity.class );
//        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//        intent.putExtra( "buildingName", theSelectedBuilding.getName() );
//        intent.putExtra( "buildingAddress", theSelectedBuilding.getAddress() );
//        intent.putExtra( "buildingDescription", theSelectedBuilding.getDescription());
//        intent.putExtra( "buildingOpenHours", theSelectedBuilding.getDate());
//        startActivity( intent );
//        Toast.makeText(this, theSelectedBuilding.getName(), Toast.LENGTH_LONG).show();
//    }

    private class MyTask extends AsyncTask<String, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
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
}
