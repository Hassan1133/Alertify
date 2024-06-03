package com.example.alertify.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.alertify.R;
import com.example.alertify.activities.dep_admin.DepAdminLoginActivity;
import com.example.alertify.activities.high_authority.HighAuthorityLoginSignupActivity;
import com.example.alertify.activities.user.UserLoginSignupActivity;
import com.example.alertify.databinding.ActivityStartUpBinding;

public class StartUpActivity extends AppCompatActivity {

    private ActivityStartUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init()
    {
        binding.userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartUpActivity.this, UserLoginSignupActivity.class));
                finish();
            }
        });

        binding.depAdminCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartUpActivity.this, DepAdminLoginActivity.class));
                finish();
            }
        });

        binding.highAuthorityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartUpActivity.this, HighAuthorityLoginSignupActivity.class));
                finish();
            }
        });
    }
}