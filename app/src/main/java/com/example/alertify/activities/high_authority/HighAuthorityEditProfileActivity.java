package com.example.alertify.activities.high_authority;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alertify.R;
import com.example.alertify.databinding.ActivityEditHighAuthorityProfileBinding;
import com.example.alertify.databinding.HighAuthorityEditNameDialogBinding;
import com.example.alertify.databinding.HighAuthorityPasswordDialogBinding;
import com.example.alertify.main_utils.AppSharedPreferences;
import com.example.alertify.main_utils.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class HighAuthorityEditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Dialog userUpdateNameDialog, userUpdatePasswordDialog;
    private DatabaseReference highAuthorityRef;

    private FirebaseUser firebaseUser;

    private ActivityEditHighAuthorityProfileBinding binding;

    private HighAuthorityPasswordDialogBinding userEditPasswordDialogBinding;

    private HighAuthorityEditNameDialogBinding userEditNameDialogBinding;

    private AppSharedPreferences appSharedPreferences;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditHighAuthorityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {

        binding.nameEditBtn.setOnClickListener(this);
        binding.passwordEditBtn.setOnClickListener(this);

        highAuthorityRef = FirebaseDatabase.getInstance().getReference("AlertifyHighAuthority");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        appSharedPreferences = new AppSharedPreferences(HighAuthorityEditProfileActivity.this);

        getProfileData(); // set method for load user data to the profile

    }

    private void getProfileData() {

        binding.userName.setText(appSharedPreferences.getString("highAuthorityName"));

        binding.userEmail.setText(appSharedPreferences.getString("highAuthorityEmail"));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nameEditBtn:
                createUserNameDialog();
                break;
            case R.id.passwordEditBtn:
                createUserPasswordDialog();
                break;
        }
    }

    private void createUserPasswordDialog() {
        userEditPasswordDialogBinding = HighAuthorityPasswordDialogBinding.inflate(LayoutInflater.from(this));
        userUpdatePasswordDialog = new Dialog(HighAuthorityEditProfileActivity.this);
        userUpdatePasswordDialog.setContentView(userEditPasswordDialogBinding.getRoot());
        userUpdatePasswordDialog.show();
        userUpdatePasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        userEditPasswordDialogBinding.userProfilePasswordDialogCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userUpdatePasswordDialog.dismiss();
            }
        });

        userEditPasswordDialogBinding.userProfilePasswordDialogUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidPassword()) {
                    loadingDialog = LoadingDialog.showLoadingDialog(HighAuthorityEditProfileActivity.this);
                    verifyHighAuthorityCurrentPassword(firebaseUser.getEmail(), userEditPasswordDialogBinding.userCurrentPassword.getText().toString().trim());
                }
            }
        });
    }

    private void verifyHighAuthorityCurrentPassword(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateUserPassword(userEditPasswordDialogBinding.userNewPassword.getText().toString().trim());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                LoadingDialog.hideLoadingDialog(loadingDialog);
                userEditPasswordDialogBinding.userCurrentPassword.setError("password is invalid");
                Toast.makeText(HighAuthorityEditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserPassword(String newPassword) {
        firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(HighAuthorityEditProfileActivity.this, "User Password Updated Successfully", Toast.LENGTH_SHORT).show();
                    LoadingDialog.hideLoadingDialog(loadingDialog);
                    userUpdatePasswordDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                LoadingDialog.hideLoadingDialog(loadingDialog);
                Toast.makeText(HighAuthorityEditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidPassword() {
        boolean valid = true;

        if (userEditPasswordDialogBinding.userCurrentPassword.getText().length() < 6) {
            userEditPasswordDialogBinding.userCurrentPassword.setError("enter valid password");
            valid = false;
        }

        if (userEditPasswordDialogBinding.userNewPassword.getText().length() < 6) {
            userEditPasswordDialogBinding.userNewPassword.setError("enter valid password");
            valid = false;
        }

        return valid;
    }

    private void createUserNameDialog() {
        userEditNameDialogBinding = HighAuthorityEditNameDialogBinding.inflate(LayoutInflater.from(this));
        userUpdateNameDialog = new Dialog(HighAuthorityEditProfileActivity.this);
        userUpdateNameDialog.setContentView(userEditNameDialogBinding.getRoot());
        userUpdateNameDialog.show();
        userUpdateNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        userEditNameDialogBinding.userDialogName.setText(appSharedPreferences.getString("highAuthorityName"));

        userEditNameDialogBinding.userEditNameDialogCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userUpdateNameDialog.dismiss();
            }
        });

        userEditNameDialogBinding.userEditNameDialogUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEditNameDialogBinding.userDialogName.getText().length() < 3) {
                    userEditNameDialogBinding.userDialogName.setError("Please enter valid name");
                } else {
                    loadingDialog = LoadingDialog.showLoadingDialog(HighAuthorityEditProfileActivity.this);
                    updateNameToDb(userEditNameDialogBinding.userDialogName.getText().toString().trim());
                }
            }
        });
    }

    private void updateNameToDb(String updatedName) {

        HashMap<String, Object> map = new HashMap<>();

        map.put("name", updatedName);

        highAuthorityRef.child(firebaseUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    LoadingDialog.hideLoadingDialog(loadingDialog);
                    Toast.makeText(HighAuthorityEditProfileActivity.this, "User Name Updated Successfully!", Toast.LENGTH_SHORT).show();
                    userUpdateNameDialog.dismiss();

                    binding.userName.setText(updatedName);
                    appSharedPreferences.put("highAuthorityName", updatedName);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HighAuthorityEditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}