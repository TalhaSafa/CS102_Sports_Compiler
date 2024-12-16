package com.example.sportscompiler;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.Positions;
import com.example.sportscompiler.AdditionalClasses.TeamType;
import com.example.sportscompiler.AdditionalClasses.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlayerScreenOfMatch5x5 extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private Match currentMatch;
    private Button confirmationButton, applyRatingButton, didntAttendButton;
    private RatingBar ratingBar;
    private FloatingActionButton[] positionButtons;
    private EditText matchScoreForTeamA, matchScoreForTeamB;
    private String matchID;
    private String matchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_screen_of_match5x5);
        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");
        confirmationButton = findViewById(R.id.confirmationButton);
        applyRatingButton = findViewById(R.id.button6);
        didntAttendButton = findViewById(R.id.button5);
        ratingBar = findViewById(R.id.ratingBar);
        positionButtons = new FloatingActionButton[]{
                findViewById(R.id.fab_player1),
                findViewById(R.id.fab_player2),
                findViewById(R.id.fab_player3),
                findViewById(R.id.fab_player4),
                findViewById(R.id.fab_player5),
        };

        matchScoreForTeamA = findViewById(R.id.matchScoreForTeamA);
        matchScoreForTeamB = findViewById(R.id.matchScoreForTeamB);

        firestore.collection(matchType).document(matchID).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentMatch = documentSnapshot.toObject(Match.class);
                    }
                });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserID = firebaseAuth.getCurrentUser().getUid();

        if(currentUserID.equals(currentMatch.getAdminID()))
        {
            confirmationButton.setVisibility(View.VISIBLE);
            applyRatingButton.setVisibility(View.GONE);
            didntAttendButton.setVisibility(View.VISIBLE);

            for(FloatingActionButton button : positionButtons)
            {
                button.setVisibility(View.VISIBLE);
            }
            matchScoreForTeamA.setEnabled(true);
            matchScoreForTeamB.setEnabled(true);
        }
        else
        {

        }
    }

    private String getPlayerIdInClickedPosition(Positions position, TeamType team)
    {
        String wantedPlayerID;
        if(team == TeamType.TEAM_A)
        {
            wantedPlayerID = currentMatch.getPlayersA().get(position).getUserID();
        }
        else
        {
            wantedPlayerID = currentMatch.getPlayersB().get(position).getUserID();
        }

        return wantedPlayerID;

    }

    private void pushRating(double rating, String userID)
    {
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                double totalRating = user.getRatingNumber() * user.getAverageRating();
                totalRating += rating;
                user.addRatingNumber();
                user.setAverageRating(totalRating / user.getRatingNumber());

                firestore.collection("users").document(userID).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PlayerScreenOfMatch5x5.this, "Rated Player", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlayerScreenOfMatch5x5.this, "Could not rate player", Toast.LENGTH_SHORT).show();
                        double totalRating = user.getRatingNumber() * user.getAverageRating();
                        totalRating -= rating;
                        user.decreaseRatingNumber();
                        user.setAverageRating(totalRating / user.getRatingNumber());

                    }

                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PlayerScreenOfMatch5x5.this, "Could not access player", Toast.LENGTH_SHORT).show();

            }
        });
    }
}