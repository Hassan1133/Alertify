package com.example.alertify.activities.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alertify.R;
import com.example.alertify.activities.dep_admin.DepAdminMainActivity;
import com.example.alertify.activities.high_authority.HighAuthorityMainActivity;
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
                boolean userCheck = appSharedPreferences.getBoolean("userFlag");
                boolean highAuthorityCheck = appSharedPreferences.getBoolean("highAuthorityLoginFlag");
                boolean depAdminCheck = appSharedPreferences.getBoolean("depAdminLogin");
                Intent intent;

                if (userCheck) {
                    intent = new Intent(SplashScreen.this, UserMainActivity.class);
                } else if (depAdminCheck) {
                    intent = new Intent(SplashScreen.this, DepAdminMainActivity.class);
                } else if (highAuthorityCheck) {
                    intent = new Intent(SplashScreen.this, HighAuthorityMainActivity.class);
                } else {
                    intent = new Intent(SplashScreen.this, StartUpActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}