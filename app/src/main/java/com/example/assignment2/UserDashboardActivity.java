package com.example.assignment2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment2.model.Site;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class UserDashboardActivity extends AppCompatActivity {
    private final String TAG = UserDashboardActivity.class.getName();
    private final int REQUEST_CODE = 101;
    TextView helloUser;
    Button createButton, joinButton, siteButton;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        helloUser = findViewById(R.id.helloUser);
        helloUser.append(user.getEmail());

        createButton = findViewById(R.id.create_button);
        joinButton = findViewById(R.id.join_button);
        siteButton = findViewById(R.id.site_button);


        docRef = db.collection("User").document(user.getEmail());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "Cached document data: " + document.getData());
                    String siteLatitude;
                    if(document.getData().get("site") == null) {
                        siteLatitude = "";
                    } else {
                        siteLatitude = document.getData().get("site").toString();
                    }

                    createButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(siteLatitude != "") {
                                Toast.makeText(UserDashboardActivity.this, "You already created site!", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), CreateSiteActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

                    siteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(siteLatitude == "") {
                                Toast.makeText(UserDashboardActivity.this, "You don't have any owned site!", Toast.LENGTH_SHORT).show();
                            } else {
                                db.collection("Site").document(siteLatitude).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot document = task.getResult();
                                        Site ownedSite = document.toObject(Site.class);

                                        Intent intent = new Intent(getApplicationContext(), OwnedSiteActivity.class);
                                        intent.putExtra("site", ownedSite);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to joinsite activity
            }
        });

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    DocumentReference updatedDocRef = documentSnapshot.getReference();
                    updatedDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.d(TAG, "Cached document data: " + document.getData());
                                String siteLatitude;
                                if(document.getData().get("site") == null) {
                                    siteLatitude = "";
                                } else {
                                    siteLatitude = document.getData().get("site").toString();
                                }

                                createButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(siteLatitude != "") {
                                            Toast.makeText(UserDashboardActivity.this, "You already created site!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), CreateSiteActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });

                                siteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(siteLatitude == "") {
                                            Toast.makeText(UserDashboardActivity.this, "You don't have any owned site!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            db.collection("Site").document(siteLatitude).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (documentSnapshot.exists()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        Site ownedSite = document.toObject(Site.class);

                                                        Intent intent = new Intent(getApplicationContext(), OwnedSiteActivity.class);
                                                        intent.putExtra("site", ownedSite);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        }
                    });

                    joinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), JoinSiteActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}