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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OwnedSiteActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityOwnedSiteBinding binding;
    private final String TAG = OwnedSiteActivity.class.getName();
    Site ownedSite;
    Button editWaste_button, download_button;
    TextView ownedSiteName, volunteersName, collectedWaste;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("User").document(user.getEmail());
    String siteLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOwnedSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "Cached document data: " + document.getData());
                    siteLatitude = document.getData().get("site").toString();
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });

        Intent intent = getIntent();
        ownedSite = (Site) intent.getSerializableExtra("site");

        ownedSiteName = findViewById(R.id.ownedSiteName);
        volunteersName = findViewById(R.id.volunteersName);
        collectedWaste = findViewById(R.id.collectedWaste);
        editWaste_button = findViewById(R.id.editWaste_button);
        download_button = findViewById(R.id.download_button);

        ownedSiteName.append(ownedSite.getName());
        collectedWaste.append(ownedSite.getCollected().toString());

        if(ownedSite.getParticipants().size() != 0) {
            for (int i = 0; i < ownedSite.getParticipants().size(); i++) {
                volunteersName.append(ownedSite.getParticipants().get(i).toString() + "\n");
            }
        } else {
            volunteersName.setText("No participants...");
        }

        editWaste_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(OwnedSiteActivity.this);

                alert.setTitle("Edit total collected waste");
                alert.setMessage("How many?");

                final EditText input = new EditText(OwnedSiteActivity.this);
                alert.setView(input);

                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer newCollected;
                        String value = input.getText().toString();
                        try {
                            newCollected = Integer.parseInt(value);

                            db.collection("Site").document(siteLatitude)
                                    .update("collected", newCollected)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(OwnedSiteActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch(NumberFormatException ex) {
                            Toast.makeText(OwnedSiteActivity.this, "Invalid format", Toast.LENGTH_SHORT).show();
                        }
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

        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ClearAway");

                if(!folder.exists()) {
                    folder.mkdirs();
                }

                StringBuilder volunteersList = new StringBuilder();
                volunteersList.append("Site name: " + ownedSite.getName() + "\n\nVolunteers list:\n");

                Integer idx = 1;

                if(ownedSite.getParticipants().isEmpty()) {
                    volunteersList.append("No participants!");
                } else {
                    for (String participantEmail : ownedSite.getParticipants()) {
                        volunteersList.append(idx + ". ").append(participantEmail).append("\n");
                        idx++;
                    }
                }

                File txtFile = new File(folder, user.getEmail() + ".txt");

                try {
                    FileWriter writer = new FileWriter(txtFile);
                    writer.append(volunteersList.toString());
                    writer.flush();
                    writer.close();

                    Toast.makeText(OwnedSiteActivity.this, "Volunteers list has been Downloaded!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

        LatLng sitePosition = new LatLng(ownedSite.getLatitude(), ownedSite.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sitePosition).title(ownedSite.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sitePosition));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sitePosition, 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}