package com.example.alertify.activities.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alertify.adapters.ViewPagerAdapter;
import com.example.alertify.databinding.ActivityUserLoginSignupBinding;
import com.example.alertify.fragments.user.UserLoginFragment;
import com.example.alertify.fragments.user.UserSignupFragment;

public class UserLoginSignupActivity extends AppCompatActivity {

    private ActivityUserLoginSignupBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserLoginSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }

    private void init() // initialization of widgets
    {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // set fragments and titles to the adapter
        viewPagerAdapter.addFragment(new UserLoginFragment(), "LOGIN");
        viewPagerAdapter.addFragment(new UserSignupFragment(), "SIGNUP");

        // set adapter on viewpager
        binding.viewPager.setAdapter(viewPagerAdapter);

        // set tabLayout with viewpager
        binding.tabsLayout.setupWithViewPager(binding.viewPager);
    }

}