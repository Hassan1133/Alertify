package com.example.alertify.fragments.dep_admin;

import static com.example.alertify.constants.Constants.ALERTIFY_DEP_ADMIN_REF;
import static com.example.alertify.constants.Constants.ALERTIFY_EMERGENCY_REQUESTS_REF;
import static com.example.alertify.constants.Constants.ALERTIFY_POLICE_STATIONS_REF;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.alertify.adapters.dep_admin.DepAdminEmergencyRequestsAdp;
import com.example.alertify.databinding.DepAdminEmergencyRequestsFragmentBinding;
import com.example.alertify.main_utils.AppSharedPreferences;
import com.example.alertify.main_utils.DistanceCalculator;
import com.example.alertify.models.EmergencyRequestModel;
import com.example.alertify.models.PoliceStationModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DepAdminEmergencyRequestsFragment extends Fragment {
    private List<EmergencyRequestModel> emergencyServiceList;
    private List<PoliceStationModel> policeStations;
    private DatabaseReference emergencyRef, depAdminRef, policeStationsRef;
    private DepAdminEmergencyRequestsFragmentBinding binding;
    private AppSharedPreferences appSharedPreferences;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double userLatitude, userLongitude;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DepAdminEmergencyRequestsFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded())
        {
            init();
        }
    }

    private void init()
    {
        emergencyRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_EMERGENCY_REQUESTS_REF);
        depAdminRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_DEP_ADMIN_REF);
        policeStationsRef = FirebaseDatabase.getInstance().getReference(ALERTIFY_POLICE_STATIONS_REF);
        appSharedPreferences = new AppSharedPreferences(requireActivity());
        binding.emergencyRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        emergencyServiceList = new ArrayList<>();
        policeStations = new ArrayList<>();

        fetchData();
    }

    private void fetchData() {

        if(!isAdded())
        {
            return;
        }

        binding.emergencyProgressbar.setVisibility(View.VISIBLE);

        emergencyServiceList.clear();

        // Listen for changes in complaintList
        depAdminRef.child(appSharedPreferences.getString("depAdminId")).child("emergencyRequestList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    emergencyServiceList.clear(); // Clear complaints to avoid duplication
                    for (DataSnapshot snapshotData : snapshot.getChildren()) {
                        String complaintID = snapshotData.getValue(String.class);
                        listenForComplaintUpdates(complaintID);
                    }
                } else {
                    binding.emergencyProgressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.emergencyProgressbar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenForComplaintUpdates(String complaintID) {
        emergencyRef.child(complaintID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    EmergencyRequestModel complaint = snapshot.getValue(EmergencyRequestModel.class);
                    if (complaint != null) {
                        int index = findComplaintIndex(complaintID);
                        if (index == -1) {
                            // New complaint
                            emergencyServiceList.add(complaint);
                        } else {
                            // Existing complaint, update it
                            emergencyServiceList.set(index, complaint);
                        }

                        emergencyServiceList.sort((complaint1, complaint2) -> {
                            return complaint2.getRequestDateTime().compareTo(complaint1.getRequestDateTime()); // Descending order
                        });
                        setDataToRecycler(emergencyServiceList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.emergencyProgressbar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int findComplaintIndex(String complaintID) {
        for (int i = 0; i < emergencyServiceList.size(); i++) {
            if (emergencyServiceList.get(i).getRequestId().equals(complaintID)) {
                return i;
            }
        }
        return -1;
    }

    private void setDataToRecycler(List<EmergencyRequestModel> emergencyServices) {

        if(!isAdded())
        {
            return;
        }

        DepAdminEmergencyRequestsAdp adp = new DepAdminEmergencyRequestsAdp(getActivity(), emergencyServices);
        binding.emergencyRecycler.setAdapter(adp);
        binding.emergencyProgressbar.setVisibility(View.GONE);

        getUnseenEmergencyRequests();
    }

    private long getMinuteDifferenceOfComplaints(String dateString) {
        try {
            Date date = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).parse(dateString);
            long diffInMillis = Calendar.getInstance().getTimeInMillis() - date.getTime();

            return TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 if there's an error parsing the date
        }
    }

    private void getUnseenEmergencyRequests() {
        if (!emergencyServiceList.isEmpty()) {
            List<String> unseenComplaintList = new ArrayList<>();

            for (EmergencyRequestModel complaint : emergencyServiceList) {
                if (complaint.getRequestStatus().equals("unseen") && !complaint.isForwarded()) {
                    long minutesDifference = getMinuteDifferenceOfComplaints(complaint.getRequestDateTime());
                    if (minutesDifference >= 2 && minutesDifference <= 3) {
                        unseenComplaintList.add(complaint.getRequestId());
                    }
                }
            }

            if (!unseenComplaintList.isEmpty()) {
                getComplaintsID(unseenComplaintList);
            }
        }
    }

    private void getComplaintsID(List<String> unseenComplaintList) {
        depAdminRef.child(appSharedPreferences.getString("depAdminId")).child("emergencyRequestList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> complaints = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    complaints.add(dataSnapshot.getValue(String.class));
                }

                complaints.removeAll(unseenComplaintList);

                updateComplaints(complaints, unseenComplaintList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateComplaints(List<String> updatedComplaintsList, List<String> unseenComplaintList) {
        depAdminRef.child(appSharedPreferences.getString("depAdminId")).child("emergencyRequestList").setValue(updatedComplaintsList).addOnSuccessListener(aVoid -> {
            getLastLocation(unseenComplaintList);
        }).addOnFailureListener(e -> {
            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void getLastLocation(List<String> unseenComplaintList) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
                getPoliceStationsData(unseenComplaintList);
            }
        });
    }

    private void getPoliceStationsData(List<String> unseenComplaintList) {
        policeStationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                policeStations.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PoliceStationModel policeStationModel = dataSnapshot.getValue(PoliceStationModel.class);
                        policeStations.add(policeStationModel);
                    }
                    calculateDistance(policeStations, unseenComplaintList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateDistance(List<PoliceStationModel> policeStationsList, List<String> unseenComplaintList) {
        PoliceStationModel nearestPoliceStation = null;
        double shortestDistance = Double.MAX_VALUE;

        for (PoliceStationModel policeStation : policeStationsList) {
            if (!policeStation.getDepAdminId().equals(appSharedPreferences.getString("depAdminId"))) {
                double distance = DistanceCalculator.calculateDistance(userLatitude, userLongitude, policeStation.getPoliceStationLatitude(), policeStation.getPoliceStationLongitude());
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearestPoliceStation = policeStation;
                }
            }
        }

        if (nearestPoliceStation != null) {
            Toast.makeText(getActivity(), nearestPoliceStation.getPoliceStationName(), Toast.LENGTH_SHORT).show();
            getNearestDepartmentAdminEmergencyRequestsID(nearestPoliceStation, unseenComplaintList);
        }
    }

    private void getNearestDepartmentAdminEmergencyRequestsID(PoliceStationModel nearestPoliceStation, List<String> unseenComplaintList) {
        depAdminRef.child(nearestPoliceStation.getDepAdminId()).child("emergencyRequestList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> complaints = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    complaints.add(dataSnapshot.getValue(String.class));
                }

                Set<String> mergedComplaints = new HashSet<>(complaints);
                mergedComplaints.addAll(unseenComplaintList);

                sendRequestToNearestDepartmentAdmin(nearestPoliceStation, new ArrayList<>(mergedComplaints));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequestToNearestDepartmentAdmin(PoliceStationModel nearestPoliceStation, List<String> updatedComplaintsList) {
        depAdminRef.child(nearestPoliceStation.getDepAdminId()).child("emergencyRequestList").setValue(updatedComplaintsList)
                .addOnSuccessListener(aVoid -> {
                    updateComplaintForwarded(nearestPoliceStation, updatedComplaintsList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateComplaintForwarded(PoliceStationModel nearestPoliceStation, List<String> updatedComplaintsList) {
        for (String complaint : updatedComplaintsList) {
            emergencyRef.child(complaint).child("forwarded").setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        fetchData();
                        getDepAdminFCMToken(nearestPoliceStation.getDepAdminId());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void getDepAdminFCMToken(String depAdminId) {
        depAdminRef.child(depAdminId).child("depAdminFCMToken").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sendNotification(task.getResult().getValue().toString(), depAdminId);
                Toast.makeText(getActivity(), "Emergency Service request forwarded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void sendNotification(String token, String depAdminId) {

        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject dataObj = new JSONObject();
            dataObj.put("title", "Muhammad Hassan Idrees");
            dataObj.put("body", "needs help right now.");
            dataObj.put("type", "userEmergency");
            dataObj.put("id", depAdminId);

            jsonObject.put("data", dataObj);
            jsonObject.put("to", token);

            callApi(jsonObject);

        } catch (Exception e) {
            Log.d("UserComplaintsFragment", "sendNotification: " + e);
        }
    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAA-aYabrI:APA91bHATVQVDYwB1qVX2_O8D1wWhVy0weiIPNJ5-G76w7WMSqcyqVs3HkOqJw8qYXlEl5YvG_62HgIyURoeNPpJN5n3v3jVeNtsGTKmle7tw7tuxxhrtpyCd0zcniEjIgb9aldbIG0l")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
        });
    }
}