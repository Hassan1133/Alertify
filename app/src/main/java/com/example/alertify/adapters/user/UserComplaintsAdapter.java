package com.example.alertify.adapters.user;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertify.R;
import com.example.alertify.activities.user.UserComplaintsDetailsActivity;
import com.example.alertify.models.ComplaintModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class UserComplaintsAdapter extends RecyclerView.Adapter<UserComplaintsAdapter.Holder> {

    private Context context;

    private List<ComplaintModel> complaintsList;

    public UserComplaintsAdapter(Context context, List<ComplaintModel> complaints) {
        this.context = context;
        complaintsList = complaints;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.complaint_recycler_design, parent, false);
        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Log.d("TAGHOLDER", "onBindViewHolder: "+position+" ---- "+complaintsList.size());
        ComplaintModel complaintModel = complaintsList.get(position);

        try {
            holder.complaintDateTime.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("dd MMM yyyy hh:mm a").parse(complaintModel.getComplaintDateTime())));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        holder.crimeType.setText(complaintModel.getCrimeType());
        holder.reportPoliceStation.setText(complaintModel.getPoliceStation());
        holder.investigationStatus.setText(complaintModel.getInvestigationStatus());
        holder.detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserComplaintsDetailsActivity.class);
                intent.putExtra("complaintModel", complaintModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return complaintsList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView complaintDateTime, crimeType, reportPoliceStation, investigationStatus;
        private ImageView detailBtn;

        public Holder(@NonNull View itemView) {
            super(itemView);

            complaintDateTime = itemView.findViewById(R.id.complaint_date_time);
            crimeType = itemView.findViewById(R.id.crime_type);
            reportPoliceStation = itemView.findViewById(R.id.report_police_station);
            detailBtn = itemView.findViewById(R.id.detail_btn);
            investigationStatus = itemView.findViewById(R.id.report_investigation_status);

        }
    }
}