package com.example.alertify.fragments.user;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.alertify.R;
import com.example.alertify.activities.common.MapsActivity;
import com.example.alertify.constants.Constants;
import com.example.alertify.databinding.FragmentUserFindPoliceStationBinding;
import com.example.alertify.main_utils.LatLngWrapper;
import com.example.alertify.main_utils.LocationPermissionUtils;
import com.example.alertify.models.PoliceStationModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class UserFindPoliceStationFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    private FragmentUserFindPoliceStationBinding binding;
    private double userLatitude, userLongitude, userCurrentLatitudeForDirections, userCurrentLongitudeForDirections;
    private double appropriatePoliceStationLatitude;
    private double appropriatePoliceStationLongitude;
    private GoogleMap googleMap;
    private ArrayList<PoliceStationModel> policeStations;
    private DatabaseReference policeStationsRef;

    private String appropriatePoliceStationLocation;

    private LocationPermissionUtils permissionUtils;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserFindPoliceStationBinding.inflate(inflater, container, false);
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
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.policeStationMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        binding.startDirectionBtn.setOnClickListener(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        policeStationsRef = FirebaseDatabase.getInstance().getReference(Constants.ALERTIFY_POLICE_STATIONS_REF);
        policeStations = new ArrayList<>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(requireActivity(), R.layout.drop_down_item, getResources().getStringArray(R.array.location_options));
        // set adapter to the autocomplete tv to the arrayAdapter
        binding.userLocation.setAdapter(arrayAdapter);

        permissionUtils = new LocationPermissionUtils(getActivity());

        dropDownSelection();
    }

    private void dropDownSelection() {
        binding.userLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                get the selected item
                String selectedItem = (String) adapterView.getItemAtPosition(i);

                if (selectedItem.matches("Your Current Location")) {
                    appropriatePoliceStationLocation = null;
                    appropriatePoliceStationLatitude = 0;
                    appropriatePoliceStationLongitude = 0;
                    userLatitude = 0;
                    userLongitude = 0;
                    googleMap.clear();
                    if (permissionUtils.isMapsEnabled()) {
                        if (permissionUtils.isLocationPermissionGranted()) {
                            getLastLocation(); // get Last Location
                        } else {
                            permissionUtils.getLocationPermission();
                        }
                    }

                } else if (selectedItem.matches("Choose on Map")) {
                    appropriatePoliceStationLocation = null;
                    appropriatePoliceStationLatitude = 0;
                    appropriatePoliceStationLongitude = 0;
                    userLatitude = 0;
                    userLongitude = 0;
                    googleMap.clear();

                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    mapsActivityResultLauncher.launch(intent);

                    }
                }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
                getPoliceStationsData();
            }
        });
    }

    private void getCurrentLocationForDirectionsAgain(double policeStationLatitude, double policeStationLongitude) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                userCurrentLatitudeForDirections = location.getLatitude();
                userCurrentLongitudeForDirections = location.getLongitude();
                if (userCurrentLatitudeForDirections != 0 && userCurrentLongitudeForDirections != 0) {
                    openGoogleMapsForDirections(userCurrentLatitudeForDirections, userCurrentLongitudeForDirections, appropriatePoliceStationLatitude, appropriatePoliceStationLongitude);
                } else {
                    Toast.makeText(getActivity(), "Please select your location again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getPoliceStationsData() {
        policeStationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(!isAdded())
                {
                    return;
                }

                policeStations.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PoliceStationModel policeStationModel = dataSnapshot.getValue(PoliceStationModel.class);
                        policeStations.add(policeStationModel);
                    }
                    getAppropriatePoliceStation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAppropriatePoliceStation() {// Find the appropriate police station
        if (policeStations != null && !policeStations.isEmpty() && userLatitude != 0 && userLongitude != 0) { // Check if the list is not null and not empty


            for (PoliceStationModel policeStation : policeStations) {

                List<LatLng> latLngPoints = convertLatLngWrapperList(policeStation.getBoundaries());

                if (isLocationInsidePoliceStationArea(new LatLng(userLatitude, userLongitude), latLngPoints)) {
                    moveCameraToAppropriatePoliceStationLocation(policeStation.getPoliceStationName(), policeStation.getPoliceStationLatitude(), policeStation.getPoliceStationLongitude());
                    appropriatePoliceStationLocation = policeStation.getPoliceStationLocation();
                    appropriatePoliceStationLatitude = policeStation.getPoliceStationLatitude();
                    appropriatePoliceStationLongitude = policeStation.getPoliceStationLongitude();
                    break;
                }
            }

            if (appropriatePoliceStationLocation == null) {
                Toast.makeText(getActivity(), "No police station found regarding your location", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean isLocationInsidePoliceStationArea(LatLng location, List<LatLng> latLngPoints) {
        if (latLngPoints != null) {
            return PolyUtil.containsLocation(location, latLngPoints, true);
        }
        return false;
    }

    private List<LatLng> convertLatLngWrapperList(List<LatLngWrapper> latLngWrapperList) {
        List<LatLng> latLngList = new ArrayList<>();

        for (LatLngWrapper latLngWrapper : latLngWrapperList) {
            LatLng latLng = new LatLng(latLngWrapper.getLatitude(), latLngWrapper.getLongitude());
            latLngList.add(latLng);
        }

        return latLngList;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.startDirectionBtn) {
            if(!isAdded())
            {
                return;
            }
            if (userLatitude != 0 && userLongitude != 0 && appropriatePoliceStationLatitude != 0 && appropriatePoliceStationLongitude != 0) {
                getCurrentLocationForDirectionsAgain(appropriatePoliceStationLatitude, appropriatePoliceStationLongitude);
            } else {
                Toast.makeText(getActivity(), "Please select your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGoogleMapsForDirections(double startLat, double startLon, double destLat, double destLon) {
        //Uri with the destination coordinates
        // http://maps.google.com/maps?saddr=37.7749,-122.4194&daddr=34.0522,-118.2437
        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=" + startLat + "," + startLon + "&daddr=" + destLat + "," + destLon);

        //Intent with the action to view and set the data to the Uri
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        //package to ensure only the Google Maps app is used
        mapIntent.setPackage("com.google.android.apps.maps");

        // Check if there is an app available to handle the Intent before starting it
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getActivity(), "Google Maps not installed in this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCameraToAppropriatePoliceStationLocation(String policeStationName, double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(policeStationName));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        assert marker != null;
        marker.showInfoWindow();
    }

    private final ActivityResultLauncher<Intent> mapsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                userLatitude = result.getData().getDoubleExtra("latitude", 0);
                userLongitude = result.getData().getDoubleExtra("longitude", 0);
                getPoliceStationsData();
            }
        }
    });

}
