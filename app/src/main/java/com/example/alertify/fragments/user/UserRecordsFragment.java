package com.example.alertify.fragments.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.alertify.R;
import com.example.alertify.activities.user.UserCrimesActivity;
import com.example.alertify.activities.user.UserCriminalsActivity;
import com.example.alertify.activities.user.UserLawsActivity;
import com.example.alertify.databinding.UserRecordsFragmentBinding;

public class UserRecordsFragment extends Fragment implements View.OnClickListener {


    private UserRecordsFragmentBinding binding;

    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = UserRecordsFragmentBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.crimes.setOnClickListener(this);

        binding.laws.setOnClickListener(this);

        binding.criminals.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.crimes:
                intent = new Intent(getActivity(), UserCrimesActivity.class);
                startActivity(intent);
                break;
            case R.id.laws:
                intent = new Intent(getActivity(), UserLawsActivity.class);
                startActivity(intent);
                break;
            case R.id.criminals:
                intent = new Intent(getActivity(), UserCriminalsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
