package com.algonquincollege.ayes0002.doorsopenottawa;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by hjalmarayestas on 2016-11-16.
 */

public class DetailActivity extends Activity{

    private TextView detailBuildingName;
    private TextView detailBuildingDescription;
    private TextView detailBuildingOpenHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailBuildingName = (TextView) findViewById( R.id.buildingName );
        detailBuildingDescription = (TextView) findViewById( R.id.buildingDescription );
        detailBuildingOpenHours = (TextView) findViewById( R.id.buildingOpenHours );

        // Get the bundle of extras that was sent to this activity
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            String buildingNameFromMainActivity = bundle.getString( "buildingName" );
            String buildingDescriptionFromMainActivity = bundle.getString( "buildingDescription" );
            String buildingOpenHoursFromMainActivity = bundle.getString( "buildingOpenHours" );

            detailBuildingName.setText( buildingNameFromMainActivity );
            detailBuildingDescription.setText( buildingDescriptionFromMainActivity );
            detailBuildingOpenHours.setText( buildingOpenHoursFromMainActivity );

        }
    }
}
