package com.example.alertify.fragments.high_authority;

import static com.example.alertify.constants.Constants.ALERTIFY_HIGH_AUTHORITY_REF;

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
import com.example.alertify.activities.high_authority.HighAuthorityMainActivity;
import com.example.alertify.databinding.HighAuthorityLoginBinding;
import com.example.alertify.main_utils.AppSharedPreferences;
import com.example.alertify.main_utils.LoadingDialog;
import com.example.alertify.models.HighAuthorityModel;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class HighAuthorityLoginFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private HighAuthorityLoginBinding binding;
    private AppSharedPreferences appSharedPreferences;
    private Dialog ladingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HighAuthorityLoginBinding.inflate(inflater, container, false);
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

        databaseReference = FirebaseDatabase.getInstance().getReference(ALERTIFY_HIGH_AUTHORITY_REF);
        appSharedPreferences = new AppSharedPreferences(requireActivity());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                if (isValid()) {
                    ladingDialog = LoadingDialog.showLoadingDialog(getActivity());
                    signIn();
                }
                break;
        }
    }

    private void signIn() {
        firebaseAuth.signInWithEmailAndPassword(binding.email.getText().toString().trim(), binding.password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    getProfileData();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                LoadingDialog.hideLoadingDialog(ladingDialog);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "The Password is wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isAdded()) {
                        Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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

    private void getProfileData() {

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    HighAuthorityModel user = snapshot.getValue(HighAuthorityModel.class);
                    if (user != null) {
                        try {
                            getFCMToken(user);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                if (isAdded()) {
                    Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void getFCMToken(HighAuthorityModel user) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    setFCMTokenToDb(task.getResult(), user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (isAdded()) {
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFCMTokenToDb(String token, HighAuthorityModel user) {
        user.setHighAuthorityFCMToken(token);

        HashMap<String, Object> map = new HashMap<>();

        map.put("highAuthorityFCMToken", user.getHighAuthorityFCMToken());

        databaseReference.child(user.getId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    goToMainActivity(user);
                }
            }
        });
    }

    private void goToMainActivity(HighAuthorityModel user) {
        appSharedPreferences.put("highAuthorityId", user.getId());
        appSharedPreferences.put("highAuthorityName", user.getName());
        appSharedPreferences.put("highAuthorityEmail", user.getEmail());
        appSharedPreferences.put("highAuthorityLoginFlag", true);
        LoadingDialog.hideLoadingDialog(ladingDialog);

        if (isAdded()) {
            Toast.makeText(requireActivity(), "Logged in Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), HighAuthorityMainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }
}
