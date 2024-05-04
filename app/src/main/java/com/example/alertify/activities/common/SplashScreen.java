package com.example.alertify.activities.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alertify.R;
import com.example.alertify.activities.user.UserLoginSignupActivity;
import com.example.alertify.activities.user.UserMainActivity;
import com.example.alertify.main_utils.AppSharedPreferences;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private AppSharedPreferences appSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach_screen);

        appSharedPreferences = new AppSharedPreferences(SplashScreen.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean check = appSharedPreferences.getBoolean("userFlag");
                Intent intent;

                if (check) {
                    intent = new Intent(SplashScreen.this, UserMainActivity.class);
                } else {
                    intent = new Intent(SplashScreen.this, UserLoginSignupActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}