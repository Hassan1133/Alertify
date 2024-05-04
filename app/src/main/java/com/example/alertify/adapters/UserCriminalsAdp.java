package com.example.alertify.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertify.R;
import com.example.alertify.activities.user.UserCriminalDetailActivity;
import com.example.alertify.models.CriminalsModel;

import java.util.List;

public class UserCriminalsAdp extends RecyclerView.Adapter<UserCriminalsAdp.Holder> {

    private Context context;

    private List<CriminalsModel> criminalsList;


    public UserCriminalsAdp(Context context, List<CriminalsModel> criminalsList) {
        this.context = context;
        this.criminalsList = criminalsList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.criminals_recycler_design, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        CriminalsModel criminalModel = criminalsList.get(position);

        holder.criminalName.setText(criminalModel.getCriminalName());
        holder.criminalCnic.setText(criminalModel.getCriminalCnic());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserCriminalDetailActivity.class);
                intent.putExtra("criminalModel", criminalModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return criminalsList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView criminalName, criminalCnic;
        public Holder(@NonNull View itemView) {
            super(itemView);
            criminalName = itemView.findViewById(R.id.criminalName);
            criminalCnic = itemView.findViewById(R.id.criminalCnic);
        }
    }
}
