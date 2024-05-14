package com.example.alertify.activities.user;

import static com.example.alertify.constants.Constants.ALERTIFY_LAWS_REF;
import static com.example.alertify.constants.Constants.LAWS_REF;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.alertify.adapters.user.UserCrimeLawsAdp;
import com.example.alertify.databinding.ActivityUserLawsDetailsBinding;
import com.example.alertify.models.CrimeLawsModel;
import com.example.alertify.models.LawsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserLawsDetailsActivity extends AppCompatActivity {

    private ActivityUserLawsDetailsBinding binding;

    private List<CrimeLawsModel> crimeLaws;

    private DatabaseReference lawsRef;

    private LawsModel lawsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserLawsDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }
    private void init()
    {
        getDataFromIntent();
        lawsRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_LAWS_REF);
        crimeLaws = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(UserLawsDetailsActivity.this));
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
                Toast.makeText(UserLawsDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToRecycler(List<CrimeLawsModel> laws) {
        UserCrimeLawsAdp adp = new UserCrimeLawsAdp(UserLawsDetailsActivity.this, laws);
        binding.recyclerView.setAdapter(adp);
    }
}