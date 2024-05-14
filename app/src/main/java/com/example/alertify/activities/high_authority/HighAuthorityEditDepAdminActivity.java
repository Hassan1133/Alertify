package com.example.alertify.activities.high_authority;

import static com.example.alertify.constants.Constants.ALERTIFY_DEP_ADMIN_REF;

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
import com.example.alertify.databinding.ActivityHighAuthorityEditDepAdminBinding;
import com.example.alertify.databinding.DepAdminEditNameDialogBinding;
import com.example.alertify.main_utils.LoadingDialog;
import com.example.alertify.models.DepAdminModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
public class HighAuthorityEditDepAdminActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHighAuthorityEditDepAdminBinding binding;
    private DepAdminModel depAdminModel;
    private Dialog depAdminUpdateNameDialog;
    private DatabaseReference depAdminRef;
    private DepAdminEditNameDialogBinding depAdminEditNameDialogBinding;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHighAuthorityEditDepAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {

        depAdminRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_DEP_ADMIN_REF);

        binding.depAdminNameEditBtn.setOnClickListener(this);

        binding.depAdminStatusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus(depAdminModel);
            }
        });

        getIntentData();
    }

    private void getIntentData() {
        depAdminModel = (DepAdminModel) getIntent().getSerializableExtra("depAdminModel");
        binding.editDepAdminName.setText(depAdminModel.getDepAdminName());
        binding.editDepAdminPoliceStation.setText(depAdminModel.getDepAdminPoliceStation());
        binding.editDepAdminEmail.setText(depAdminModel.getDepAdminEmail());
        if (depAdminModel.getDepAdminStatus().equals("unblock")) {
            binding.depAdminStatusIcon.setImageResource(R.drawable.unlock);
        } else if (depAdminModel.getDepAdminStatus().equals("block")) {
            binding.depAdminStatusIcon.setImageResource(R.drawable.lock);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.depAdminNameEditBtn) {
            createDepAdminNameDialog();
        }
    }
    private void createDepAdminNameDialog() {
        depAdminEditNameDialogBinding = DepAdminEditNameDialogBinding.inflate(LayoutInflater.from(this));
        depAdminUpdateNameDialog = new Dialog(HighAuthorityEditDepAdminActivity.this);
        depAdminUpdateNameDialog.setContentView(depAdminEditNameDialogBinding.getRoot());
        depAdminUpdateNameDialog.show();
        depAdminUpdateNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        depAdminEditNameDialogBinding.editDepAdminDialogName.setText(depAdminModel.getDepAdminName());

        depAdminEditNameDialogBinding.editDepAdminNameDialogCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                depAdminUpdateNameDialog.dismiss();
            }
        });

        depAdminEditNameDialogBinding.editDepAdminUpdateNameDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (depAdminEditNameDialogBinding.editDepAdminDialogName.getText().length() < 3) {
                    depAdminEditNameDialogBinding.editDepAdminDialogName.setError("Please enter valid name");
                } else {
                    loadingDialog = LoadingDialog.showLoadingDialog(HighAuthorityEditDepAdminActivity.this);
                    updateNameToDb(depAdminEditNameDialogBinding.editDepAdminDialogName.getText().toString().trim());
                }
            }
        });
    }

    private void updateNameToDb(String updatedName) {
        depAdminModel.setDepAdminName(updatedName);

        HashMap<String, Object> map = new HashMap<>();

        map.put("depAdminName", depAdminModel.getDepAdminName());

        depAdminRef.child(depAdminModel.getDepAdminId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    LoadingDialog.hideLoadingDialog(loadingDialog);
                    Toast.makeText(HighAuthorityEditDepAdminActivity.this, "Department Admin Name Updated Successfully!", Toast.LENGTH_SHORT).show();
                    depAdminUpdateNameDialog.dismiss();

                    binding.editDepAdminName.setText(depAdminModel.getDepAdminName());

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HighAuthorityEditDepAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(DepAdminModel depAdminModel) {
        if (depAdminModel.getDepAdminStatus().equals("unblock")) {

            depAdminModel.setDepAdminStatus("block");

            HashMap<String, Object> map = new HashMap<>();

            map.put("depAdminStatus", depAdminModel.getDepAdminStatus());

            depAdminRef.child(depAdminModel.getDepAdminId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        binding.depAdminStatusIcon.setImageResource(R.drawable.lock);
                        Toast.makeText(HighAuthorityEditDepAdminActivity.this, "Department Admin Blocked Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HighAuthorityEditDepAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (depAdminModel.getDepAdminStatus().equals("block")) {

            depAdminModel.setDepAdminStatus("unblock");

            HashMap<String, Object> map = new HashMap<>();

            map.put("depAdminStatus", depAdminModel.getDepAdminStatus());

            depAdminRef.child(depAdminModel.getDepAdminId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        binding.depAdminStatusIcon.setImageResource(R.drawable.unlock);
                        Toast.makeText(HighAuthorityEditDepAdminActivity.this, "Department Admin UnBlocked Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HighAuthorityEditDepAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}