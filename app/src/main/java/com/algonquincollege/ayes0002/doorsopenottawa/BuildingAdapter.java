package com.algonquincollege.ayes0002.doorsopenottawa;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.algonquincollege.ayes0002.doorsopenottawa.model.Building;

import java.util.List;

/**
 * Usage:
 *   1) extend from class ArrayAdapter<YourModelClass>
 *   2) @override getView( ) :: decorate the list cell
 *
 * Based on the Adapter OO Design Pattern.
 *
 * @author ayes0002@algonquinlive.com
 *
 * Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */

public class BuildingAdapter extends ArrayAdapter<Building>{

    private Context context;
    private List<Building> buildingList;

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);

        //Display planet name in the TextView widget
        Building building = buildingList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(building.getName() + "\n" + building.getDate());

        return view;
    }
}
