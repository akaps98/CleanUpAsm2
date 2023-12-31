package com.example.assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.assignment2.model.Site;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.assignment2.databinding.ActivityCreateSiteBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CreateSiteActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = CreateSiteActivity.class.getName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private GoogleMap mMap;
    private ActivityCreateSiteBinding binding;
    protected FusedLocationProviderClient client;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        client = LocationServices.getFusedLocationProviderClient(CreateSiteActivity.this);
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        getLastLocation();

        db.collection("Site").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ArrayList<Site> allSites = new ArrayList<>();
                                Site site = document.toObject(Site.class);
                                allSites.add(site);

                                Marker[] markers = new Marker[allSites.size()];

                                for(int i = 0; i < allSites.size(); i++) {
                                    markers[i] = createMarker(allSites.get(i).getLatitude(), allSites.get(i).getLongitude(), allSites.get(i).getName());
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                String message = "Site name: " + marker.getTitle();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                Toast.makeText(CreateSiteActivity.this, message, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;

                AlertDialog.Builder alert = new AlertDialog.Builder(CreateSiteActivity.this);

                alert.setTitle("Create your own site");
                alert.setMessage("What is site name?");

                final EditText input = new EditText(CreateSiteActivity.this);
                alert.setView(input);

                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String siteName = input.getText().toString();

                        Site newSite = new Site(latitude, longitude, siteName, user.getEmail());

                        db.collection("Site").document(String.valueOf(latitude))
                                .set(newSite).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        db.collection("User").document(user.getEmail())
                                            .update("site", latitude).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(CreateSiteActivity.this, "You have created your own site!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                        finish();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });

    }

    private void moveCameraToCurrentLocation(Location location) {
        String message = "Your current location (Red Marker)";
        LatLng curLoc = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(curLoc).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 16));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        client.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            moveCameraToCurrentLocation(location);
                        }
                    }
                });
    }

    private Bitmap resizeBitmap(String imgName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(imgName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    protected Marker createMarker(double latitude, double longitude, String title) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .anchor(0.5f, 0.5f)
                //.snippet(snippet)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(String.valueOf(R.drawable.custom_map_icon),250,140))));

        return marker;
    }
}