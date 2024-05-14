package com.example.alertify.fragments.dep_admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.alertify.adapters.dep_admin.DepAdminEmergencyRequestsAdp;
import com.example.alertify.databinding.DepAdminEmergencyRequestsFragmentBinding;
import com.example.alertify.models.EmergencyRequestModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepAdminEmergencyRequestsFragment extends Fragment {
    private List<EmergencyRequestModel> emergencyServiceList;
    private DepAdminEmergencyRequestsAdp adp;
    private DatabaseReference emergencyRef;
    private DepAdminEmergencyRequestsFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DepAdminEmergencyRequestsFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded())
        {
            init();
        }
    }

    private void init()
    {
        emergencyRef = FirebaseDatabase.getInstance().getReference("AlertifyEmergencyRequests");

        binding.emergencyRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        emergencyServiceList = new ArrayList<EmergencyRequestModel>();

        fetchData();
    }

    private void fetchData() {

        if(!isAdded())
        {
            return;
        }

        binding.emergencyProgressbar.setVisibility(View.VISIBLE);
        emergencyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                emergencyServiceList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EmergencyRequestModel emergencyRequestModel = dataSnapshot.getValue(EmergencyRequestModel.class);
                    if(emergencyRequestModel != null)
                    {
                        emergencyServiceList.add(emergencyRequestModel);
                    }
                }

                // Sort the list
                Collections.reverse(emergencyServiceList);
                setDataToRecycler(emergencyServiceList);

                binding.emergencyProgressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.emergencyProgressbar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToRecycler(List<EmergencyRequestModel> emergencyServices) {

        if(!isAdded())
        {
            return;
        }

        adp = new DepAdminEmergencyRequestsAdp(getActivity(), emergencyServices);
        binding.emergencyRecycler.setAdapter(adp);
    }
}