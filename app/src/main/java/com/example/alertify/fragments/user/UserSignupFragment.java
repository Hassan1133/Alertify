package com.example.alertify.fragments.user;

import static com.example.alertify.constants.Constants.USERS_REF;

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
import com.example.alertify.activities.user.UserLoginSignupActivity;
import com.example.alertify.databinding.FragmentUserSignupBinding;
import com.example.alertify.main_utils.LoadingDialog;
import com.example.alertify.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserSignupFragment extends Fragment implements View.OnClickListener {

    private FragmentUserSignupBinding binding;

    private UserModel user;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference firebaseDatabaseReference;

    private Dialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(isAdded())
        {
            init();
        }
    }

    private void init() // method for widgets or variables initialization
    {
        binding.signupBtn.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(USERS_REF);

    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.signupBtn) {
            checkUserCnicPhoneExists(binding.cnic.getText().toString().trim(), binding.phone.getText().toString().trim());
        }
    }

    private void checkUserCnicPhoneExists(String cnicNo, String phoneNo) {

        firebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            int count = 0;
            boolean check = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!isAdded())
                {
                    return;
                }

                if (snapshot.exists()) {

                    // data exists in database
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                        UserModel user = userSnapshot.getValue(UserModel.class);

                        count++;

                        if (user.getPhoneNo().equals(phoneNo) || user.getCnicNo().equals(cnicNo)) {
                            if (user.getPhoneNo().equals(phoneNo)) {
                                binding.phone.setText("");
                                Toast.makeText(getActivity(), "Phone number already exists. Please choose a different one", Toast.LENGTH_SHORT).show();
                                binding.phone.setError("Phone number already exists. Please choose a different one");
                                check = true;
                            }
                            if (user.getCnicNo().equals(cnicNo)) {
                                binding.cnic.setText("");
                                Toast.makeText(getActivity(), "CNIC number already exists. Please choose a different one", Toast.LENGTH_SHORT).show();
                                binding.cnic.setError("CNIC number already exists. Please choose a different one");
                                check = true;
                            }
                            return;
                        } else if (count == snapshot.getChildrenCount()) {
                            if (!check) {
                                createAccount();
                            }
                        }
                    }
                } else {
                    createAccount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount() // method for create account
    {

        if (isValid()) {

            loadingDialog = LoadingDialog.showLoadingDialog(getActivity());

            List<String> complaintList = new ArrayList<>();

            user = new UserModel();
            user.setName(binding.name.getText().toString().trim());
            user.setPhoneNo(binding.phone.getText().toString().trim());
            user.setCnicNo(binding.cnic.getText().toString().trim());
            user.setEmail(binding.email.getText().toString().trim());
            user.setComplaintList(complaintList);
            user.setType("user");
            user.setUserStatus("unblock");

            firebaseAuth
                    .createUserWithEmailAndPassword(user.getEmail(), binding.password.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user.setId(firebaseAuth.getUid());
                                addToDB(user);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            LoadingDialog.hideLoadingDialog(loadingDialog);
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void addToDB(@NonNull UserModel user) // method for add data to the database
    {
        firebaseDatabaseReference
                .child(user.getId())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            if(!isAdded())
                            {
                                return;
                            }
                            LoadingDialog.hideLoadingDialog(loadingDialog);
                            Toast.makeText(getContext(), "Signed up Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), UserLoginSignupActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LoadingDialog.hideLoadingDialog(loadingDialog);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValid() // method for data validation
    {
        boolean valid = true;

        if (binding.name.getText().length() < 3) {
            binding.name.setError("enter valid name");
            valid = false;
        }
        if (binding.phone.getText().length() < 11) {
            binding.phone.setError("enter valid phone no");
            valid = false;
        }
        if (binding.cnic.getText().length() < 13 || binding.cnic.getText().length() > 13) {
            binding.cnic.setError("enter valid cnic no");
            valid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText()).matches()) {
            binding.email.setError("enter valid email");
            valid = false;
        }
        if (binding.password.getText().length() < 6) {
            binding.password.setError("enter valid password");
            valid = false;
        }

        return valid;
    }

}
