package com.example.alertify.adapters.high_authority;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertify.R;
import com.example.alertify.activities.high_authority.HighAuthorityEditDepAdminActivity;
import com.example.alertify.models.DepAdminModel;

import java.util.List;

public class HighAuthorityDepAdminAdp extends RecyclerView.Adapter<HighAuthorityDepAdminAdp.Holder> {
    private final Context context;

    private final List<DepAdminModel> depAdminsList;

    public HighAuthorityDepAdminAdp(Context context, List<DepAdminModel> depAdmins) {
        this.context = context;
        depAdminsList = depAdmins;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dep_admin_recycler_design, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        DepAdminModel depAdminModel = depAdminsList.get(position);

        if(depAdminModel != null)
        {
            if (depAdminModel.getDepAdminStatus().equals("block")) {
                holder.depAdminName.setText(depAdminModel.getDepAdminName());
                holder.depAdminPoliceStation.setText(depAdminModel.getDepAdminPoliceStation());
                holder.blockBtn.setVisibility(View.VISIBLE);
            } else {
                holder.depAdminName.setText(depAdminModel.getDepAdminName());
                holder.depAdminPoliceStation.setText(depAdminModel.getDepAdminPoliceStation());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HighAuthorityEditDepAdminActivity.class);
                    intent.putExtra("depAdminModel", depAdminModel);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return depAdminsList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final TextView depAdminName;
        private final TextView depAdminPoliceStation;

        private final ImageView blockBtn;

        public Holder(@NonNull View itemView) {
            super(itemView);
            depAdminName = itemView.findViewById(R.id.dep_admin_name);
            depAdminPoliceStation = itemView.findViewById(R.id.dep_admin_police_station);
            blockBtn = itemView.findViewById(R.id.block_icon);
        }
    }
}
