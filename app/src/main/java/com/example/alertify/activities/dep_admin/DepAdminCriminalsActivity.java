package com.example.alertify.activities.dep_admin;


import static com.example.alertify.constants.Constants.ALERTIFY_CRIMINALS_REF;

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
import com.example.alertify.adapters.dep_admin.DepAdminCriminalsAdp;
import com.example.alertify.databinding.ActivityDepAdminCriminalsBinding;
import com.example.alertify.models.CriminalsModel;
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

public class DepAdminCriminalsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityDepAdminCriminalsBinding binding;

    private Dialog criminalsDialog;

    private TextInputEditText criminalCnic, criminalName;
    private ProgressBar criminalDialogProgressbar;

    private DatabaseReference criminalsRef;

    private List<CriminalsModel> criminals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDepAdminCriminalsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        binding.addBtn.setOnClickListener(this);
        criminalsRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_CRIMINALS_REF);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(DepAdminCriminalsActivity.this));
        criminals = new ArrayList<CriminalsModel>();

        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });

        fetchCriminalData();
    }

    private void search(String newText) {
        ArrayList<CriminalsModel> searchList = new ArrayList<>();
        for (CriminalsModel i : criminals) {
            if (i.getCriminalName().toLowerCase().contains(newText.toLowerCase()) || i.getCriminalCnic().contains(newText)) {
                searchList.add(i);
            }
        }
        setDataToRecycler(searchList);
    }

    private void fetchCriminalData() {
        binding.progressbar.setVisibility(View.VISIBLE);

        criminalsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                criminals.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    criminals.add(dataSnapshot.getValue(CriminalsModel.class));
                }

                binding.progressbar.setVisibility(View.GONE);

                setDataToRecycler(criminals);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DepAdminCriminalsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addBtn) {
            createAddCriminalDialog();
        }
    }

    private void createAddCriminalDialog() {
        criminalsDialog = new Dialog(this);
        criminalsDialog.setContentView(R.layout.add_criminal_dialog);
        criminalsDialog.setCancelable(false);
        criminalsDialog.show();
        criminalsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        criminalDialogProgressbar = criminalsDialog.findViewById(R.id.dialogProgressbar);
        criminalCnic = criminalsDialog.findViewById(R.id.criminalCnic);
        criminalName = criminalsDialog.findViewById(R.id.criminalName);

        criminalsDialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criminalsDialog.dismiss();
            }
        });

        criminalsDialog.findViewById(R.id.addCriminalBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {
                    criminalDialogProgressbar.setVisibility(View.VISIBLE);

                    CriminalsModel criminalsModel = new CriminalsModel();
                    criminalsModel.setCriminalCnic(criminalCnic.getText().toString());
                    criminalsModel.setCriminalName(criminalName.getText().toString());
                    checkCriminalAlreadyExistOrNot(criminalsModel);

                }

            }
        });
    }

    private void checkCriminalAlreadyExistOrNot(CriminalsModel criminalsModel) {
        criminalsRef.child(criminalsModel.getCriminalCnic()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    criminalDialogProgressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(DepAdminCriminalsActivity.this, "Criminal already exists. Please enter a different one", Toast.LENGTH_SHORT).show();
                    criminalCnic.setError("Criminal exists. Please enter a different one");
                }
                else {
                    addToDb(criminalsModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DepAdminCriminalsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addToDb(CriminalsModel criminalsModel) {
        criminalsRef.child(criminalsModel.getCriminalCnic()).setValue(criminalsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DepAdminCriminalsActivity.this, "Criminal added successfully", Toast.LENGTH_SHORT).show();
                    criminalDialogProgressbar.setVisibility(View.INVISIBLE);
                    criminalsDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DepAdminCriminalsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                criminalDialogProgressbar.setVisibility(View.INVISIBLE);
                criminalsDialog.dismiss();
            }
        });
    }

    private boolean isValid() {
        boolean valid = true;

        if (criminalCnic.getText().length() < 13) {
            criminalCnic.setError("Please enter valid CNIC");
            valid = false;
        }
        if (criminalName.getText().length() < 3) {
            criminalName.setError("Please enter valid name");
            valid = false;
        }
        return valid;
    }

    private void setDataToRecycler(List<CriminalsModel> criminalsList) {
        DepAdminCriminalsAdp adp = new DepAdminCriminalsAdp(DepAdminCriminalsActivity.this, criminalsList);
        binding.recyclerView.setAdapter(adp);
    }
}