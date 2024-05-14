package com.example.alertify.adapters.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alertify.R;
import com.example.alertify.models.CrimeLawsModel;

import java.util.List;

public class UserCrimeLawsAdp extends RecyclerView.Adapter<UserCrimeLawsAdp.Holder> {

    private final Context context;

    private final List<CrimeLawsModel> crimesLawsList;


    public UserCrimeLawsAdp(Context context, List<CrimeLawsModel> crimesLawsList) {
        this.context = context;
        this.crimesLawsList = crimesLawsList;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.crime_laws_recycler_design, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        CrimeLawsModel crimesLawModel = crimesLawsList.get(position);


        holder.sectionNo.setText(crimesLawModel.getSectionNumber());
        holder.law.setText(crimesLawModel.getLaw());


    }

    @Override
    public int getItemCount() {
        return crimesLawsList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView sectionNo, law;

        public Holder(@NonNull View itemView) {
            super(itemView);
            sectionNo = itemView.findViewById(R.id.sectionNumber);
            law = itemView.findViewById(R.id.law);
        }
    }
}
