package com.example.alertify.fragments.dep_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.alertify.R;
import com.example.alertify.activities.dep_admin.DepAdminCrimesActivity;
import com.example.alertify.activities.dep_admin.DepAdminCriminalsActivity;
import com.example.alertify.activities.dep_admin.DepAdminLawsActivity;
import com.example.alertify.databinding.DepAdminRecordsFragmentBinding;

public class DepAdminRecordsFragment extends Fragment implements View.OnClickListener {

    private Intent intent;

    private DepAdminRecordsFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DepAdminRecordsFragmentBinding.inflate(inflater, container, false);
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

    private void init() {
        binding.crimes.setOnClickListener(this);
        binding.criminals.setOnClickListener(this);
        binding.laws.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.crimes) {
            intent = new Intent(getActivity(), DepAdminCrimesActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.criminals) {
            intent = new Intent(getActivity(), DepAdminCriminalsActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.laws) {
            intent = new Intent(getActivity(), DepAdminLawsActivity.class);
            startActivity(intent);
        }
    }
}
