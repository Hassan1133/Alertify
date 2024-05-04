package com.example.alertify.activities.user;

import static com.example.alertify.constants.Constants.ALERTIFY_CRIMINALS_REF;
import static com.example.alertify.constants.Constants.FIR_REF;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.alertify.adapters.UserCriminalCrimesAdp;
import com.example.alertify.databinding.ActivityUserCriminalDetailBinding;
import com.example.alertify.models.CriminalCrimesModel;
import com.example.alertify.models.CriminalsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserCriminalDetailActivity extends AppCompatActivity {

    private ActivityUserCriminalDetailBinding binding;

    private CriminalsModel criminalsModel;

    private DatabaseReference criminalsRef;

    private List<CriminalCrimesModel> criminalCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserCriminalDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        getDataFromIntent();
        criminalsRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_CRIMINALS_REF);
        criminalCrimes = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(UserCriminalDetailActivity.this));
        fetchFIR();
    }

    private void fetchFIR() {
        criminalsRef.child(criminalsModel.getCriminalCnic()).child(FIR_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    criminalCrimes.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        criminalCrimes.add(dataSnapshot.getValue(CriminalCrimesModel.class));
                    }

                    setDataToRecycler(criminalCrimes);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserCriminalDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToRecycler(List<CriminalCrimesModel> criminalCrimes) {
        UserCriminalCrimesAdp adp = new UserCriminalCrimesAdp(UserCriminalDetailActivity.this, criminalCrimes);
        binding.recyclerView.setAdapter(adp);
    }

    private void getDataFromIntent() {
        criminalsModel = (CriminalsModel) getIntent().getSerializableExtra("criminalModel");
        assert criminalsModel != null;
        binding.criminalName.setText(criminalsModel.getCriminalName());
        binding.criminalCnic.setText(criminalsModel.getCriminalCnic());

    }
}