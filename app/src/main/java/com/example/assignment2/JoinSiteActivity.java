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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.example.assignment2.databinding.ActivityJoinSiteBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JoinSiteActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = JoinSiteActivity.class.getName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private float zoomLevel = 16.0f;
    private GoogleMap mMap;
    private ActivityJoinSiteBinding binding;
    protected FusedLocationProviderClient client;
    SearchView searchView;
    Spinner zoomLevelDropdown;
    ArrayList<Site> allSites = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityJoinSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db.collection("Site").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Site site = document.toObject(Site.class);
                                allSites.add(site);
                            }
                        }
                    }
                });

        searchView = findViewById(R.id.searchView);
        zoomLevelDropdown = findViewById(R.id.zoomLevelDropdown);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // search function
            @Override
            public boolean onQueryTextSubmit(String query) {
                boolean isFound = false;
                for (Site site : allSites) { // if the query(searching words) is equal to real site name, it moves camera to that site.
                    if (site.getName().toLowerCase().contains(query.toLowerCase(Locale.ROOT))) {
                        LatLng siteLatLng = new LatLng(site.getLatitude(), site.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(siteLatLng, 16));
                        isFound = true;
                    }
                }

                if(!isFound) { // query is not equal to any site name
                    Toast.makeText(JoinSiteActivity.this, "No site!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        zoomLevelDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // placeholder
                        break;
                    case 1: // More narrow
                        zoomLevel = 22.0f;
                        break;
                    case 2: // Less narrow
                        zoomLevel = 19.0f;
                        break;
                    case 3: // Less wide
                        zoomLevel = 16.0f;
                        break;
                    case 4: // More wide
                        zoomLevel = 12.0f;
                        break;
                }

                if (ActivityCompat.checkSelfPermission(JoinSiteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(JoinSiteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(JoinSiteActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    return;
                }

                client.getLastLocation()
                    .addOnSuccessListener(JoinSiteActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
                            }
                        }
                    });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        mMap = googleMap;

        client = LocationServices.getFusedLocationProviderClient(JoinSiteActivity.this);
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        getCurrentLocation();

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

                                for(int i = 0; i < allSites.size(); i++) { // create custom markers according to the location of registered sites
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
                db.collection("Site").document(String.valueOf(marker.getPosition().latitude))
                        .get() .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Site site = documentSnapshot.toObject(Site.class);
                                    String ownerName = site.getOwner();

                                    // create dialog message to display details of site to user
                                    String message = "Site name: " + marker.getTitle()
                                                    + "\nOwner: " + ownerName
                                                    + "\nLatitude: " + marker.getPosition().latitude
                                                    + "\nLongitude: " + marker.getPosition().longitude;

                                    AlertDialog.Builder alert = new AlertDialog.Builder(JoinSiteActivity.this);

                                    alert.setTitle("Join this site?");
                                    alert.setMessage(message);

                                    alert.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db.collection("Site").document(String.valueOf(marker.getPosition().latitude))
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            ArrayList<String> alreadyParticipants = (ArrayList<String>) documentSnapshot.get("participants");

                                                            if (site.getOwner().equals(user.getEmail())) {
                                                                Toast.makeText(JoinSiteActivity.this, "You are an owner of this site!\nDecline to join.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                if (alreadyParticipants != null && alreadyParticipants.contains(user.getEmail())) { // if the user is already join on selected site
                                                                    Toast.makeText(JoinSiteActivity.this, "You already joined at this site!\nDecline to join.", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Map<String, Object> userJoin = new HashMap<>();
                                                                    userJoin.put("participants", FieldValue.arrayUnion(user.getEmail()));

                                                                    db.collection("Site").document(String.valueOf(marker.getPosition().latitude))
                                                                            .update(userJoin) // update Site db to join user
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(JoinSiteActivity.this, "Success to join!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                        }
                                    });

                                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    alert.show();
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                                }
                            }
                        });
                return false;
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

    private void getCurrentLocation() {
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