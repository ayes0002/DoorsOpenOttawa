package com.algonquincollege.ayes0002.doorsopenottawa.parsers;

import android.util.Log;

import com.algonquincollege.ayes0002.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com ayes0002@algonquinlive.com
 *
 * Reference: FlowerJSONParser in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */

public class BuildingJSONParser {
    public static List<Building> parseFeed(String content) {

        try {
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray buildingArray = jsonResponse.getJSONArray("buildings");
            List<Building> buildingList = new ArrayList<>();

            for (int i = 0; i < buildingArray.length(); i++) {

                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();

                building.setAddress(obj.getString("address"));
                building.setBuildingId(obj.getInt("buildingId"));
                building.setImage(obj.getString("image"));
                building.setName(obj.getString("name"));
                building.setOpen_hours(obj.getJSONArray("open_hours"));
                buildingList.add(building);
            }

            return buildingList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
