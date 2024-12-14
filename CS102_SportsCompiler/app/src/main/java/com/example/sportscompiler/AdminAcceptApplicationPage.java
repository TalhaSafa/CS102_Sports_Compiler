package com.example.sportscompiler;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdminAcceptApplicationPage extends AppCompatActivity {

    private Button matchForumButton;
    private firestoreUser fireuser;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Match currentMatch;
    private String matchID, matchType;
    private Timestamp date;
    private long dateTimeMillis;
    private RecyclerView recyclerView;
    private ApplicationsAdapter appAdapt;
    private ArrayList<Application> applications;
    private ApplicationActionListener actionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_accept_application_page);

        recyclerView = findViewById(R.id.applicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        matchForumButton = findViewById(R.id.ForumPage);

        firebaseAuth = FirebaseAuth.getInstance();
        fireuser = new firestoreUser();
        user = new User();
        firestore = FirebaseFirestore.getInstance();
        initializeUser();

        matchID = getIntent().getStringExtra("matchID");
        System.out.println(matchID);
        matchType = getIntent().getStringExtra("matchType");
        System.out.println(matchType);
        fetchMatchFromFirestore(matchID);

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

    private void initializeForum(Timestamp date, View view)
    {
        String messageID = matchID +  user.getName() + date.toDate().toString();
        Message startingMessage = new Message(user, date, user.getName() + " created this match."
                , messageID);

        firestore.collection(currentMatch.getMatchType()).document(matchID).collection("forum")
                .document("SystemMessage").set(startingMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!(task.isSuccessful()))
                        {
                            Toast.makeText(view.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            FragmentLoad.changeActivity((Activity) view.getContext(), MatchForumActivity.class);
                        }
                    }
                });
    }
    private void fetchMatchFromFirestore(String matchID) {
        String type = "matches6";
        if(matchType.equals("matches5")){
            type = "matches5";
        }
        firestore.collection(type).document(matchID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentMatch = documentSnapshot.toObject(Match.class);
                    if (currentMatch != null) {
                        fillApplications();
                    } else {
                        Log.e("fetchMatchFromFirestore", "Match object is null!");
                    }
                })
                .addOnFailureListener(e -> Log.e("fetchMatchFromFirestore", "Failed to fetch match!", e));
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
                    appAdapt = new ApplicationsAdapter(applications, actionListener);
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


}