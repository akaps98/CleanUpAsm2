package com.example.assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment2.databinding.ActivitySiteDetailBinding;
import com.example.assignment2.model.Site;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.assignment2.databinding.ActivityOwnedSiteBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiteDetailActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivitySiteDetailBinding binding;
    private final String TAG = OwnedSiteActivity.class.getName();
    Site detailSite;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView siteDetailName, ownedUserName, detailCollectedWaste, detailVolunteersName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySiteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        detailSite = (Site) intent.getSerializableExtra("site");

        db.collection("Site").document(String.valueOf(detailSite.getLatitude()))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Site site = document.toObject(Site.class);
                        detailSite = site;
                    }
                });

        siteDetailName = findViewById(R.id.siteDetailName);
        ownedUserName = findViewById(R.id.ownedUserName);
        detailCollectedWaste = findViewById(R.id.detailCollectedWaste);
        detailVolunteersName = findViewById(R.id.detailVolunteersName);

        siteDetailName.setText(detailSite.getName());
        ownedUserName.append(detailSite.getOwner());
        detailCollectedWaste.append(detailSite.getCollected().toString());

        if(detailSite.getParticipants().size() != 0) {
            for (int i = 0; i < detailSite.getParticipants().size(); i++) {
                detailVolunteersName.append(detailSite.getParticipants().get(i).toString() + "\n");
            }
        } else {
            detailVolunteersName.setText("No participants...");
        }
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

        // Add a marker in Sydney and move the camera
        LatLng sitePosition = new LatLng(detailSite.getLatitude(), detailSite.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sitePosition).title(detailSite.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sitePosition));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sitePosition, 16));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}