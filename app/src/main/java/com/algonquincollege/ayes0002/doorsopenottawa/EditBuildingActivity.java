package com.algonquincollege.ayes0002.doorsopenottawa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.ayes0002.doorsopenottawa.parsers.HttpMethod;
import com.algonquincollege.ayes0002.doorsopenottawa.parsers.RequestPackage;

import org.w3c.dom.Text;

/**
 * Created by hjalmarayestas on 2016-12-13.
 */

public class EditBuildingActivity extends AppCompatActivity {


    private String EBuildingDescriptionR;
    private String EBuildingAddressR;
    private EditText EBuildingDescription;
    private TextView EBuildingName;
    private EditText EBuildingAddress;
    private Button saveB;
    private Button cancelB;
    private Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbuilding);

        EBuildingName = (TextView) findViewById(R.id.editBuildingName_tv);
        EBuildingDescription = (EditText) findViewById(R.id.editBuildingDescription);
        EBuildingAddress = (EditText) findViewById(R.id.editBuildingAddress);
        saveB = (Button) findViewById(R.id.editSaveButton);
        cancelB = (Button) findViewById(R.id.editCancelButton);

        saveB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EBuildingAddressR = EBuildingAddress.getText().toString();
                EBuildingDescriptionR = EBuildingDescription.getText().toString();

                RequestPackage pkg = new RequestPackage();
                pkg.setMethod(HttpMethod.PUT);
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    id = bundle.getInt("id");
                    EBuildingName.setText(bundle.getString("name"));
                    pkg.setUri(MainActivity.REST_URI + "/" + id);
                    pkg.setParam("address", EBuildingAddressR);
                    pkg.setParam("description", "@author Hjalmar Ayestas (ayes0002)");
                }
                EditBuildingActivity.DoTask postTask = new EditBuildingActivity.DoTask();
                postTask.execute(pkg);
                Intent editBuildingIntent = new Intent( getApplicationContext(), MainActivity.class );
                startActivity( editBuildingIntent );
            }
        });

        cancelB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent editBuildingIntent = new Intent(getApplicationContext(), DetailActivity.class);
            }
        });

    }

    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = HttpManager.getData(params[0], "ayes0002", "password");
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result == null) {
                Toast.makeText(EditBuildingActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}
