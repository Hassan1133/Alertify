package com.example.alertify.activities.high_authority;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alertify.adapters.user.ViewPagerAdapter;
import com.example.alertify.databinding.ActivityHighAuthorityLoginSignupBinding;
import com.example.alertify.fragments.high_authority.HighAuthorityLoginFragment;
import com.example.alertify.fragments.high_authority.HighAuthoritySignupFragment;


public class HighAuthorityLoginSignupActivity extends AppCompatActivity {

    private ActivityHighAuthorityLoginSignupBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHighAuthorityLoginSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

    }

    private void init() // initialization of widgets
    {

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // set fragments and titles to the adapter
        viewPagerAdapter.addFragment(new HighAuthorityLoginFragment(), "LOGIN");
        viewPagerAdapter.addFragment(new HighAuthoritySignupFragment(), "SIGNUP");

        // set adapter on viewpager
        binding.viewPager.setAdapter(viewPagerAdapter);

        // set tabLayout with viewpager
        binding.tabsLayout.setupWithViewPager(binding.viewPager);
    }

}