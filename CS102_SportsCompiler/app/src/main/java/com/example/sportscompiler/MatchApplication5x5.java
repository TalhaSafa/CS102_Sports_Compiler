package com.example.sportscompiler;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MatchApplication5x5 extends AppCompatActivity {

    private FloatingActionButton kaleciA, ortadefansA, solortaA, sagortaA, ortaforvetA
            ,kaleciB, ortadefansB, solortaB, sagortaB, ortaforvetB;
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
        setContentView(R.layout.activity_match_application5x5);

        fireUser = new firestoreUser();
        firebaseAuth = FirebaseAuth.getInstance();
        user = new User();
        initializeUser();
        applicationNoteStr = "";
        applyButton = findViewById(R.id.applyButton);
        applicatonNote = findViewById(R.id.applicationNote);

        kaleciA = findViewById(R.id.kaleciA);
        ortadefansA = findViewById(R.id.ortadefansA);
        solortaA = findViewById(R.id.solKanatA);
        sagortaA = findViewById(R.id.sagKanatA);
        ortaforvetA = findViewById(R.id.ortaforvetA);

        kaleciB = findViewById(R.id.kaleciB);
        ortadefansB = findViewById(R.id.ortadefansB);
        solortaB = findViewById(R.id.solkanatB);
        sagortaB = findViewById(R.id.sagkanatB);
        ortaforvetB = findViewById(R.id.ortaforvetB);

        matchName = findViewById(R.id.matchName);
        dateTxt = findViewById(R.id.Date);
        adminName = findViewById(R.id.adminName);
        matchNote = findViewById(R.id.matchNote);
        firestore = FirebaseFirestore.getInstance();

        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");
        firestore.collection(matchType).document(matchID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Toast.makeText(MatchApplication5x5.this, "Could not access database", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(value != null && value.exists())
                {
                    match = value.toObject(Match.class);
                }
                else
                {
                    Toast.makeText(MatchApplication5x5.this, "Null Match", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MatchApplication5x5.this, "Choose position", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Application newApplication = new Application(user.getName(), positionToApply, applicationNoteStr, team, user.getUserID() );
                    match.addApplication(newApplication);
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



        }


    }
    private void initializeUser()
    {
        fireUser.updateInfo(user, new firestoreUser.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                user = result;
            }

            @Override
            public void onError(Exception e) {
                Log.e("MatchApplication", "Error fetching user info", e);
            }
        });
    }
}