package com.example.sportscompiler;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportscompiler.AdditionalClasses.Application;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.Positions;
import com.example.sportscompiler.AdditionalClasses.TeamType;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MatchApplication6x6 extends AppCompatActivity {
    private FloatingActionButton kaleciA, ortadefansA, ortasahaA, solortaA, sagortaA, ortaforvetA
            ,kaleciB, ortadefansB, ortasahaB, solortaB, sagortaB, ortaforvetB;
    private Button applyButton;
    private EditText applicatonNote;
    private String matchID, matchType, applicationNoteStr;
    private Match match;
    private Positions positionToApply;
    private TeamType team;
    private TextView matchName, dateTxt, adminName, matchNote;
    private FirebaseFirestore firestore;
    private firestoreUser fireUser;
    private FirebaseAuth firebaseAuth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_match_application6x6);

        fireUser = new firestoreUser();
        firebaseAuth = FirebaseAuth.getInstance();

        applyButton = findViewById(R.id.applyButton);
        applicatonNote = findViewById(R.id.applicationNote);
        user = new User();
        initializeUser();
        applicationNoteStr = "";

        kaleciA = findViewById(R.id.kaleciA);
        ortadefansA = findViewById(R.id.ortadefansA);
        solortaA = findViewById(R.id.solKanatA);
        ortasahaA = findViewById(R.id.ortasahaA);
        sagortaA = findViewById(R.id.sagKanatA);
        ortaforvetA = findViewById(R.id.ortaforvetA);

        kaleciB = findViewById(R.id.kaleciB);
        ortadefansB = findViewById(R.id.ortadefansB);
        solortaB = findViewById(R.id.solkanatB);
        ortasahaB = findViewById(R.id.ortasahaB);
        sagortaB = findViewById(R.id.sagkanatB);
        ortaforvetB = findViewById(R.id.ortaforvetB);

        matchName = findViewById(R.id.matchName);
        dateTxt = findViewById(R.id.Date);
        adminName = findViewById(R.id.adminName);
        matchNote = findViewById(R.id.matchNote);
        firestore = FirebaseFirestore.getInstance();

        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");
        applicatonNote.setText(matchID + " - " + matchType);
        firestore.collection(matchType).document(matchID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Toast.makeText(MatchApplication6x6.this, "Could not access database", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(value != null && value.exists())
                {
                    match = value.toObject(Match.class);
                }
                else
                {
                    Toast.makeText(MatchApplication6x6.this, "Null Match", Toast.LENGTH_SHORT).show();
                }
            }
        });


        FABListener listener = new FABListener();
        kaleciA.setOnClickListener(listener);
        kaleciB.setOnClickListener(listener);
        sagortaB.setOnClickListener(listener);
        sagortaA.setOnClickListener(listener);
        ortadefansB.setOnClickListener(listener);
        ortadefansA.setOnClickListener(listener);
        ortasahaA.setOnClickListener(listener);
        ortasahaB.setOnClickListener(listener);
        solortaB.setOnClickListener(listener);
        solortaA.setOnClickListener(listener);
        ortaforvetA.setOnClickListener(listener);
        ortaforvetB.setOnClickListener(listener);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(match != null)
                {
                    if(positionToApply == null)
                    {
                        Toast.makeText(MatchApplication6x6.this, "Choose position", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    applicationNoteStr = applicatonNote.getText().toString();
                    Application newApplication = new Application(user.getName(), positionToApply, applicationNoteStr, team, user.getUserID(), user.getAverageRating() );
                    match.addApplication(newApplication);   
                    firestore.collection(matchType).document(matchID).set(match).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MatchApplication6x6.this, "Application sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MatchApplication6x6.this, "Match not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    private class FABListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            if (view.getId() == kaleciA.getId())
            {
                positionToApply = Positions.GK1;
                team = TeamType.TEAM_A;
            }
            else if (view.getId() == kaleciB.getId())
            {
                positionToApply = Positions.GK1;
                team = TeamType.TEAM_B;
            }
            else if (view.getId() == ortadefansB.getId())
            {
                positionToApply = Positions.CB3;
                team = TeamType.TEAM_B;
            }
            else if (view.getId() == ortadefansA.getId())
            {
                positionToApply = Positions.CB3;
                team = TeamType.TEAM_A;
            }
            else if (view.getId() == solortaB.getId())
            {
                positionToApply = Positions.MO1;
                team = TeamType.TEAM_B;
            }
            else if (view.getId() == sagortaB.getId())
            {
                positionToApply = Positions.MO2;
                team = TeamType.TEAM_B;
            }
            else if (view.getId() == sagortaA.getId())
            {
                positionToApply = Positions.MO2;
                team = TeamType.TEAM_A;
            }
            else if (view.getId() == solortaA.getId())
            {
                positionToApply = Positions.MO1;
                team = TeamType.TEAM_A;
            }
            else if (view.getId() == ortaforvetB.getId())
            {
                positionToApply = Positions.FW3;
                team = TeamType.TEAM_B;
            }
            else if (view.getId() == ortaforvetA.getId())
            {
                positionToApply = Positions.FW3;
                team = TeamType.TEAM_A;
            }
            else if(view.getId() == ortasahaA.getId())
            {
                positionToApply = Positions.MO3;
                team = TeamType.TEAM_A;
            }
            else if(view.getId() == ortasahaB.getId())
            {
                positionToApply = Positions.MO3;
                team = TeamType.TEAM_B;
            }



        }


    }

    private void initializeUser() {
        applyButton.setEnabled(false); // Disable apply button initially

        fireUser.updateInfo(user, new firestoreUser.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                user = result;
                Log.d("initializeUser", "User info successfully retrieved: " + user.getName());
                applyButton.setEnabled(true); // Enable apply button once user data is ready
            }

            @Override
            public void onError(Exception e) {
                Log.e("MatchApplication", "Error fetching user info", e);
                Toast.makeText(MatchApplication6x6.this, "Failed to fetch user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}