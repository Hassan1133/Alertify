package com.example.alertify.activities.user;

import static com.example.alertify.constants.Constants.ALERTIFY_CRIMINALS_REF;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.alertify.adapters.UserCriminalsAdp;
import com.example.alertify.databinding.ActivityUserCriminalsBinding;
import com.example.alertify.models.CriminalsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserCriminalsActivity extends AppCompatActivity {

    private DatabaseReference criminalsRef;
    private List<CriminalsModel> criminals;
    private ActivityUserCriminalsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserCriminalsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        criminalsRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_CRIMINALS_REF);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(UserCriminalsActivity.this));
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
                Toast.makeText(UserCriminalsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToRecycler(List<CriminalsModel> criminalsList) {
        UserCriminalsAdp adp = new UserCriminalsAdp(UserCriminalsActivity.this, criminalsList);
        binding.recyclerView.setAdapter(adp);
    }
}