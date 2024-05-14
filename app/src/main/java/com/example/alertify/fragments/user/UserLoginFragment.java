package com.example.alertify.fragments.user;

import static com.example.alertify.constants.Constants.USERS_REF;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.alertify.R;
import com.example.alertify.activities.user.UserMainActivity;
import com.example.alertify.databinding.FragmentUserLoginBinding;
import com.example.alertify.main_utils.AppSharedPreferences;
import com.example.alertify.main_utils.LoadingDialog;
import com.example.alertify.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLoginFragment extends Fragment implements View.OnClickListener {

    private FragmentUserLoginBinding binding;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference userRef;

    private UserModel user;

    private Dialog loadingDialog;

    private AppSharedPreferences appSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isAdded()) {
            init();
        }
    }

    private void init() {
        binding.loginBtn.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        userRef = FirebaseDatabase.getInstance().getReference(USERS_REF);

        user = new UserModel();

        appSharedPreferences = new AppSharedPreferences(requireActivity());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                if (isValid()) {
                    loadingDialog = LoadingDialog.showLoadingDialog(getActivity());
                    checkForSignIn(binding.email.getText().toString().trim());
                }
                break;
        }
    }

    private void checkForSignIn(String emailText) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            int count = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!isAdded()) {
                    return;
                }

                if (snapshot.exists()) {
                    for (DataSnapshot userSnapShot : snapshot.getChildren()) {
                        UserModel userModel = userSnapShot.getValue(UserModel.class);

                        count++;

                        if (userModel.getEmail().equals(emailText) && userModel.getType().equals("user") && userModel.getUserStatus().equals("unblock")) {
                            user.setId(userModel.getId());
                            user.setEmail(userModel.getEmail());
                            user.setName(userModel.getName());
                            user.setPhoneNo(userModel.getPhoneNo());
                            user.setCnicNo(userModel.getCnicNo());
                            signIn();
                            return;
                        } else if (count == snapshot.getChildrenCount()) {
                            LoadingDialog.hideLoadingDialog(loadingDialog);
                            Toast.makeText(getActivity(), "Account doesn't exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    LoadingDialog.hideLoadingDialog(loadingDialog);
                    Toast.makeText(getActivity(), "Account doesn't exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void signIn() {
        firebaseAuth
                .signInWithEmailAndPassword(binding.email.getText().toString().trim(), binding.password.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getProfileData();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LoadingDialog.hideLoadingDialog(loadingDialog);
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getContext(), "The Password is wrong", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValid() // method for data validation
    {
        boolean valid = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText()).matches()) {
            binding.email.setError("enter valid email");
            valid = false;
        }
        if (binding.password.getText().length() < 6) {
            binding.password.setError("enter valid name");
            valid = false;
        }

        return valid;
    }

    private void goToMainActivity() {

        if (!isAdded()) {
            return;
        }

        appSharedPreferences.put("userFlag", true);
        Intent intent = new Intent(getActivity(), UserMainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void getProfileData() {

        if (!isAdded()) {
            return;
        }

        if (user != null && !user.getId().isEmpty()) {
            appSharedPreferences.put("userId", user.getId());
            appSharedPreferences.put("userName", user.getName());
            appSharedPreferences.put("userEmail", user.getEmail());
            appSharedPreferences.put("userPhoneNo", user.getPhoneNo());
            appSharedPreferences.put("userCnicNo", user.getCnicNo());

            LoadingDialog.hideLoadingDialog(loadingDialog);
            Toast.makeText(getContext(), "Logged in Successfully", Toast.LENGTH_SHORT).show();
            goToMainActivity();
        }

    }

}
