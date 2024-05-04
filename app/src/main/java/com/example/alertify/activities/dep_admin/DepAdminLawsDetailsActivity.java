package com.example.alertify.activities.dep_admin;

import static com.example.alertify.constants.Constants.ALERTIFY_LAWS_REF;
import static com.example.alertify.constants.Constants.LAWS_REF;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.alertify.R;
import com.example.alertify.adapters.dep_admin.DepAdminCrimeLawsAdp;
import com.example.alertify.databinding.ActivityDepAdminLawsDetailsBinding;
import com.example.alertify.models.CrimeLawsModel;
import com.example.alertify.models.LawsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DepAdminLawsDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityDepAdminLawsDetailsBinding binding;
    private LawsModel lawsModel;

    private Dialog addLawDialog;

    private TextInputEditText sectionNoField, lawField;

    private DatabaseReference lawsRef;

    private ProgressBar progressBar;

    private List<CrimeLawsModel> crimeLaws;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDepAdminLawsDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }
    private void init()
    {
        getDataFromIntent();
        binding.addLawBtn.setOnClickListener(this);
        lawsRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_LAWS_REF);
        crimeLaws = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(DepAdminLawsDetailsActivity.this));
        fetchLaws();
    }

    private void getDataFromIntent() {
        lawsModel = (LawsModel) getIntent().getSerializableExtra("lawsModel");
        assert lawsModel != null;
        binding.crimeType.setText(lawsModel.getCrimeType());
    }

    private void fetchLaws() {
        lawsRef.child(lawsModel.getCrimeType()).child(LAWS_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    crimeLaws.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        crimeLaws.add(dataSnapshot.getValue(CrimeLawsModel.class));
                    }

                    setDataToRecycler(crimeLaws);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DepAdminLawsDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToRecycler(List<CrimeLawsModel> laws) {
        DepAdminCrimeLawsAdp adp = new DepAdminCrimeLawsAdp(DepAdminLawsDetailsActivity.this, laws);
        binding.recyclerView.setAdapter(adp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addLawBtn) {
            createLawDialog();
        }
    }

    private void createLawDialog() {
        addLawDialog = new Dialog(DepAdminLawsDetailsActivity.this);
        addLawDialog.setContentView(R.layout.crime_law_dialog_design);
        addLawDialog.show();
        addLawDialog.setCancelable(false);
        addLawDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        lawField = addLawDialog.findViewById(R.id.law);
        sectionNoField = addLawDialog.findViewById(R.id.sectionNumber);
        progressBar = addLawDialog.findViewById(R.id.law_dialog_progressbar);

        addLawDialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLawDialog.dismiss();
            }
        });

        addLawDialog.findViewById(R.id.addLawDetailBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    progressBar.setVisibility(View.VISIBLE);
                    addLawDataToModel(sectionNoField.getText().toString(), lawField.getText().toString());
                }
            }
        });

    }

    private void addLawDataToModel(String sectionNo, String law) {
        CrimeLawsModel crimeLawsModel = new CrimeLawsModel();
        crimeLawsModel.setSectionNumber(sectionNo);
        crimeLawsModel.setLaw(law);

        addDataToDb(crimeLawsModel);
    }

    private void addDataToDb(CrimeLawsModel crimeLawsModel) {

        String id = lawsRef.push().getKey();

        crimeLawsModel.setId(id);

        assert id != null;
        lawsRef.child(lawsModel.getCrimeType()).child(LAWS_REF).child(id).setValue(crimeLawsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DepAdminLawsDetailsActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                    addLawDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DepAdminLawsDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid() {
        boolean valid = true;

        if (sectionNoField.getText().length() < 1) {
            sectionNoField.setError("enter valid section number");
            valid = false;
        }
        if (lawField.getText().length() < 3) {
            lawField.setError("enter valid law");
            valid = false;
        }
        return valid;
    }

}