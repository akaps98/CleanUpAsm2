package com.example.assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.assignment2.model.Site;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    TextView totalVolunteers, totalCollected;
    ListView siteListView;
    ArrayAdapter<Site> adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        totalVolunteers = findViewById(R.id.totalVolunteers);
        totalCollected = findViewById(R.id.totalCollected);

        siteListView = findViewById(R.id.siteListView);

        db.collection("Site")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Site> allSites = new ArrayList<>();
                        Integer numCollected = 0;
                        Integer numVolunteers = 0;

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Site site = document.toObject(Site.class);
                                allSites.add(site);
                            }

                            for(Site site : allSites) {
                                numCollected += site.getCollected();
                                if(site.getParticipants() != null) {
                                    for(String paritipant : site.getParticipants()) {
                                        numVolunteers++;
                                    }
                                }
                            }

                            totalCollected.append(numCollected.toString());
                            totalVolunteers.append(numVolunteers.toString());

                            adapter = new ArrayAdapter<Site>(AdminActivity.this, android.R.layout.simple_list_item_1, allSites) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);

                                    TextView siteText = view.findViewById(android.R.id.text1);

                                    siteText.setText(allSites.get(position).getName());
                                    siteText.setTextSize(18);

                                    Typeface tf = Typeface.createFromAsset(getResources().getAssets(), "font/aller_itit.ttf");
                                    siteText.setTypeface(tf);

                                    siteText.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                                    siteText.setGravity(android.view.Gravity.CENTER);

                                    return view;
                                }
                            };

                            siteListView.setAdapter(adapter);

                            siteListView.setOnItemClickListener((parent, view, position, id) -> {
                                Site selectedSite = allSites.get(position);
                                Intent intent = new Intent(AdminActivity.this, SiteDetailActivity.class);
                                intent.putExtra("site", selectedSite);
                                startActivity(intent);
                            });

                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
