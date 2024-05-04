package com.example.alertify.activities.user;

import static com.example.alertify.constants.Constants.ALERTIFY_CRIMES_REF;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.alertify.adapters.UserCrimesAdp;
import com.example.alertify.databinding.ActivityUserCrimesBinding;
import com.example.alertify.models.CrimesModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserCrimesActivity extends AppCompatActivity {

    private ActivityUserCrimesBinding binding;
    private DatabaseReference crimesRef;

    private List<CrimesModel> crimes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserCrimesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }
    private void init()
    {
        crimesRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_CRIMES_REF);

        binding.crimesRecycler.setLayoutManager(new LinearLayoutManager(UserCrimesActivity.this));

        crimes = new ArrayList<CrimesModel>();

        binding.crimesSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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

        fetchData();
    }

    private void search(String newText) {
        ArrayList<CrimesModel> searchList = new ArrayList<>();
        for (CrimesModel i : crimes) {
            if (i.getCrimeType().toLowerCase().contains(newText.toLowerCase()) || i.getDefinition().toLowerCase().contains(newText.toLowerCase())) {
                searchList.add(i);
            }
        }
        setDataToRecycler(searchList);
    }

    private void fetchData() {

        binding.crimesProgressbar.setVisibility(View.VISIBLE);

        crimesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                crimes.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    crimes.add(dataSnapshot.getValue(CrimesModel.class));
                }

                binding.crimesProgressbar.setVisibility(View.GONE);

                setDataToRecycler(crimes);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserCrimesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToRecycler(List<CrimesModel> crimes) {
        UserCrimesAdp adp = new UserCrimesAdp(UserCrimesActivity.this, crimes);
        binding.crimesRecycler.setAdapter(adp);
    }
}