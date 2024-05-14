package com.example.alertify.adapters.high_authority;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.alertify.R;
import com.example.alertify.interfaces.OnDropDownItemClickListener;
import com.example.alertify.models.PoliceStationModel;

import java.util.List;

public class HighAuthorityDropDownAdapter extends ArrayAdapter<PoliceStationModel> {

    private final Context context;
    private List<PoliceStationModel> policeStationList;
    private OnDropDownItemClickListener listener;

    public HighAuthorityDropDownAdapter(Context context, List<PoliceStationModel> policeStationList, OnDropDownItemClickListener listener) {
        super(context, R.layout.drop_down_item, policeStationList);
        this.context = context;
        this.policeStationList = policeStationList;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            // Inflate layout for dropdown item if convertView is null
            view = LayoutInflater.from(context).inflate(R.layout.drop_down_item, parent, false);
        }

        // Get PoliceStationModel object at the specified position
        PoliceStationModel policeStationModel = getItem(position);

        // Find TextView in the dropdown item layout
        TextView textView = view.findViewById(R.id.itemName);

        // Set text of the TextView to the name of the PoliceStationModel object
        if (policeStationModel != null) {
            textView.setText(policeStationModel.getPoliceStationName());
        }

        // Set click listener for the TextView to handle item clicks
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (policeStationModel != null) {
                    listener.onItemClick(policeStationModel.getId(), policeStationModel.getPoliceStationName()); // Pass item ID and name to the listener
                }
            }
        });

        return view;
    }
}