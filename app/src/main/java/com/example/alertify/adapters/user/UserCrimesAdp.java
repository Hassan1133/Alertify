package com.example.alertify.adapters.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertify.R;
import com.example.alertify.models.CrimesModel;

import java.util.List;

public class UserCrimesAdp extends RecyclerView.Adapter<UserCrimesAdp.Holder> {

    private final Context context;

    private final List<CrimesModel> crimesList;

    public UserCrimesAdp(Context context, List<CrimesModel> crimes) {
        this.context = context;
        crimesList = crimes;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.crimes_recycler_design, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        CrimesModel crimesModel = crimesList.get(position);

        holder.crime.setText(crimesModel.getCrimeType());
        holder.definition.setText(crimesModel.getDefinition());

    }

    @Override
    public int getItemCount() {
        return crimesList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView crime, definition;
        public Holder(@NonNull View itemView) {
            super(itemView);
            crime = itemView.findViewById(R.id.crime);
            definition = itemView.findViewById(R.id.definition);
        }
    }
}