package com.algonquincollege.ayes0002.doorsopenottawa.model;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;

/**
 * Created by hjalmarayestas on 2016-11-08.
 */

public class Building {

    private int buildingId;
    private String name;
    private String address;
    private String image;
    private JSONArray open_hours;
    private String date = "";
    private Bitmap bitmap;
    private String description;

    // GETTERS
    public int getBuildingId() { return buildingId; }
    public String getAddress() { return address; }
    public String getImage() { return image; }
    public String getName() { return name; }
    public JSONArray getOpen_hours() { return open_hours; }
    public String getDate(){ return date; }
    public Bitmap getBitmap() { return bitmap; }
    public String getDescription() { return description; }

    // SETTERS
    public void setAddress( String address ) {
        this.address = address + " Ottawa, Ontario";
    }
    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setOpen_hours(JSONArray open_hours) {
        this.open_hours = open_hours;
        date = "";
        for (int i=0; i<open_hours.length(); i++){
            try {
                date += open_hours.getJSONObject(i).getString("date") + "\n";
            } catch (JSONException e) {

            }
        }
    }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
    public void setDescription(String description) { this.description = description; }


}