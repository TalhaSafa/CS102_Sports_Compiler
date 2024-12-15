package com.example.sportscompiler;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.AdditionalClasses.Application;
import com.example.sportscompiler.AdditionalClasses.ApplicationActionListener;
import com.example.sportscompiler.AdditionalClasses.ApplicationsAdapter;
import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.Message;
import com.example.sportscompiler.AdditionalClasses.Player;
import com.example.sportscompiler.AdditionalClasses.TeamType;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class AdminAcceptApplicationPage extends AppCompatActivity {

    private Button matchForumButton;
    private firestoreUser fireuser;
    private User user;
    private FirebaseFirestore firestore;
    private Match currentMatch;
    private String matchID, matchType;
    private Timestamp date;
    private long dateTimeMillis;
    private RecyclerView recyclerView;
    private ApplicationsAdapter appAdapt;
    private ArrayList<Application> applications;
    private ApplicationActionListener actionListener;
    private TextView matchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_accept_application_page);

        recyclerView = findViewById(R.id.applicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        matchForumButton = findViewById(R.id.ForumPage);
        matchName = findViewById(R.id.matchName);

        fireuser = new firestoreUser();
        user = new User();
        firestore = FirebaseFirestore.getInstance();
        initializeUser();

        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");

        fetchMatchFromFirestore(matchID, matchType);

        matchForumButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminAcceptApplicationPage.this, MatchForumActivity.class);
                intent.putExtra("matchID", matchID);
                intent.putExtra("matchType", matchType);
                startActivity(intent);
            }
        });

    }

    public void setMatch(Match match)
    {
        this.currentMatch = match;
    }

    private void initializeUser()
    {
        fireuser.updateInfo(user, new firestoreUser.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                user = result;
            }

            @Override
            public void onError(Exception e) {
                Log.e("HomePage", "Error fetching user info", e);
            }
        });
    }

    private void fetchMatchFromFirestore(String matchID, String matchType) {

        firestore.collection(matchType).document(matchID).addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Log.e("fetchMatchFromFirestore", "Error fetching match!", error);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                currentMatch = documentSnapshot.toObject(Match.class);
                if (currentMatch != null) {
                    fillApplications(); // Update the applications list in real-time
                } else {
                    Log.e("fetchMatchFromFirestore", "Match object is null!");
                }
            } else {
                Log.e("fetchMatchFromFirestore", "No document found or document is null!");
            }
        });
    }


    private void fillApplications() {
        // Ensure the applications list is initialized
        if (applications == null) {
            applications = new ArrayList<>();
        }

        // Clear the existing list to avoid duplicates
        applications.clear();

        if (currentMatch != null) {
            ArrayList<Application> matchApplications = currentMatch.getApplications();
            if (matchApplications != null && !matchApplications.isEmpty()) {
                applications.addAll(matchApplications);

                // Notify adapter of changes (or initialize it if necessary)
                if (appAdapt == null) {
                    appAdapt = new ApplicationsAdapter(applications, new ApplicationActionListener() {
                        @Override
                        public void onAccept(Application application) {
                            //TODO EREN BURAYI TODO BIRAKMADIGIN ICIN IKI SAAT BURAYI ARADIM,TESEKKURLER,

                            if(application.getTeamInfo() == TeamType.TEAM_A)
                            {
                                addToTeam(currentMatch.getPlayersA(), application);
                            }
                            else
                            {
                                addToTeam(currentMatch.getPlayersA(), application);

                            }
                        }

                        @Override
                        public void onDecline(Application application) {
                            firestore.collection(matchType)
                                    .document(matchID)
                                    .update("applications", FieldValue.arrayRemove(application)) // Remove application from array
                                    .addOnSuccessListener(aVoid -> {
                                        applications.remove(application); // Update local list
                                        appAdapt.notifyDataSetChanged(); // Refresh UI
                                        Toast.makeText(AdminAcceptApplicationPage.this, "Application declined and removed.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Log.e("onDecline", "Failed to remove application", e));
                        }
                    });
                    recyclerView.setAdapter(appAdapt); // Set adapter after data is fetched
                } else {
                    appAdapt.notifyDataSetChanged();
                }
            } else {
                Log.e("fillApplications", "No applications found in currentMatch.");
            }
        } else {
            Log.e("fillApplications", "currentMatch is null, cannot populate applications.");
        }
    }

    private void addToTeam(Map<String, Player> playersOfWantedTeam, Application application)
    {

        //TODO CHECK
        if(playersOfWantedTeam.get(application.getPosition().getAction()) == null)
        {

            Player acceptedPlayer = new Player(application.getUserID(), application.getAverage(), application.getTeamInfo(), application.getPosition()
                ,false, currentMatch.getMatchID(), application.getName() );
            playersOfWantedTeam.put(application.getPosition().getAction(), acceptedPlayer);
            applications.remove(application); // Update local list
            appAdapt.notifyDataSetChanged(); // Refresh UI

            //currentMatch.addApplication(newApplication);
            firestore.collection(matchType).document(matchID).set(currentMatch).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(AdminAcceptApplicationPage.this, "Application Accepted", Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(appAdapt); // Set adapter after data is fetched

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    applications.add(application);
                    appAdapt.notifyDataSetChanged(); // Refresh UI

                    Toast.makeText(AdminAcceptApplicationPage.this, "Match not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(AdminAcceptApplicationPage.this, "This position is already full!", Toast.LENGTH_SHORT).show();

        }


    }


}