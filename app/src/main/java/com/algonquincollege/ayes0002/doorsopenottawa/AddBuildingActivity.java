package com.algonquincollege.ayes0002.doorsopenottawa;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.ayes0002.doorsopenottawa.parsers.HttpMethod;
import com.algonquincollege.ayes0002.doorsopenottawa.parsers.RequestPackage;

/**
 * Created by hjalmarayestas on 2016-12-09.
 */

public class AddBuildingActivity extends AppCompatActivity{

    private String NBuildingName;
    private String NBuildingDescription;
    private String NBuildingAddress;
    private Button NBuildingImage;
    private EditText NBuildingNametv;
    private EditText NBuildingDescriptiontv;
    private EditText NBuildingAddresstv;
    private ImageView NBuildingImg;
    private Button SaveNBuilding;


    private Uri selectedImage;

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbuilding);

        NBuildingNametv = (EditText) findViewById(R.id.BName_tv);

        NBuildingDescriptiontv = (EditText) findViewById(R.id.BDescription_tv);

        NBuildingAddresstv = (EditText) findViewById(R.id.BAddress_tv);

        NBuildingImage = (Button) findViewById(R.id.BPictureButton);
        SaveNBuilding = (Button) findViewById(R.id.saveButton);

        NBuildingImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loadImagefromGallery(v);
            }
        });

        SaveNBuilding.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NBuildingName = NBuildingNametv.getText().toString();
                NBuildingDescription = NBuildingDescriptiontv.getText().toString();
                NBuildingAddress = NBuildingAddresstv.getText().toString();
                SaveBuilding(v);
            }
        });
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                selectedImage = data.getData();
                Toast.makeText(AddBuildingActivity.this, selectedImage + "", Toast.LENGTH_LONG).show();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                NBuildingImg = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                NBuildingImg.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void SaveBuilding(View view){
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod(HttpMethod.POST);
        pkg.setUri(MainActivity.REST_URI);
        pkg.setParam("name", NBuildingName);
        if(selectedImage == null){
            pkg.setParam("image", "whatever.png");
        } else {
            pkg.setParam("image", selectedImage.toString());
        }
        pkg.setParam("description", NBuildingDescription);
        pkg.setParam("address", NBuildingAddress);
//            pkg.setParam("open_hours", myBuilding.getOpen_hours() + "");

        DoTask postTask = new DoTask();
        postTask.execute(pkg);
        Intent newBuildingIntent = new Intent( getApplicationContext(), MainActivity.class );
        startActivity( newBuildingIntent );
        Toast.makeText(AddBuildingActivity.this, NBuildingName, Toast.LENGTH_LONG).show();
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
                Toast.makeText(AddBuildingActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}