package com.example.sportscompiler;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private Button confirmationButton;
    private FloatingActionButton[] positionButtons;
    private EditText matchScoreForTeamA, matchScoreForTeamB;
    private String matchID;
    private String matchType;
    private ImageView fieldImage;
    private TextView enterMatchScore;
    private int selectedPosition = -1;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_screen_of_match5x5);
        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");
        confirmationButton = findViewById(R.id.confirmationButton);
        positionButtons = new FloatingActionButton[]{
                findViewById(R.id.fab_player1),
                findViewById(R.id.fab_player2),
                findViewById(R.id.fab_player3),
                findViewById(R.id.fab_player4),
                findViewById(R.id.fab_player5),
        };
        matchScoreForTeamA = findViewById(R.id.matchScoreForTeamA);
        matchScoreForTeamB = findViewById(R.id.matchScoreForTeamB);

        fieldImage = findViewById(R.id.footballField);
        enterMatchScore = findViewById(R.id.enterMatchScore);
        firestore = FirebaseFirestore.getInstance();

        firestore.collection(matchType).document(matchID).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot != null) {
                        currentMatch = documentSnapshot.toObject(Match.class);
                        setVisibilites();
                    }
                });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String scoreAStr = matchScoreForTeamA.getText().toString();
                String scoreBStr = matchScoreForTeamB.getText().toString();
                if(!scoreAStr.equals("") && !scoreBStr.equals(""))
                {
                    setScore(Integer.parseInt(scoreAStr), Integer.parseInt(scoreBStr));
                }
                else
                {
                    Toast.makeText(PlayerScreenOfMatch5x5.this, "Enter Match Score", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void setVisibilites()
    {
        if(currentUserID.equals(currentMatch.getAdminID()))
        {
            confirmationButton.setVisibility(View.VISIBLE);

            for(FloatingActionButton button : positionButtons)
            {
                button.setVisibility(View.VISIBLE);
            }
            matchScoreForTeamA.setText(Integer.toString(currentMatch.getTeamAScore()));
            matchScoreForTeamB.setText(Integer.toString(currentMatch.getTeamBScore()));
            matchScoreForTeamA.setEnabled(true);
            matchScoreForTeamB.setEnabled(true);
            fieldImage.setVisibility(View.VISIBLE);
            enterMatchScore.setVisibility(View.VISIBLE);

            for(int i = 0; i < positionButtons.length; i++)
            {
                int index = i;

                positionButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        onPositionSelected(index);
                        RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
                        ratingDialogFragment.show(getSupportFragmentManager().beginTransaction(), "Rating Dialog");
                    }
                });
            }

        }
        else
        {
            confirmationButton.setVisibility(View.GONE);

            for(FloatingActionButton button : positionButtons)
            {
                button.setVisibility(View.VISIBLE);
            }
            matchScoreForTeamA.setText(Integer.toString(currentMatch.getTeamAScore()));
            matchScoreForTeamB.setText(Integer.toString(currentMatch.getTeamBScore()));
            matchScoreForTeamA.setEnabled(false);
            matchScoreForTeamB.setEnabled(false);
            fieldImage.setVisibility(View.VISIBLE);
            enterMatchScore.setVisibility(View.GONE);
        }
    }

    private void setScore(int scoreA, int scoreB)
    {
        if(currentMatch.getMatchType() != null && currentMatch.getMatchID() != null)
        {
            currentMatch.setTeamAScore(scoreA);
            currentMatch.setTeamBScore(scoreB);
            firestore.collection(currentMatch.getMatchType()).document(currentMatch.getMatchID()).set(currentMatch).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(PlayerScreenOfMatch5x5.this, "Match Score Updated!", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PlayerScreenOfMatch5x5.this, "Could Not Update Match Score!", Toast.LENGTH_SHORT).show();
                    currentMatch.setTeamAScore(0);
                    currentMatch.setTeamBScore(0);
                }
            });
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

    private Positions determinePosition(int selectedPosition , int numberOfPlayersInATeam)
    {
        Positions position = Positions.GK1;
        if(numberOfPlayersInATeam == 5)
        {
            switch (selectedPosition)
            {
                case 4:
                    position = Positions.GK1;
                    break;
                case 2:
                    position = Positions.MO3;
                    break;
                case 1:
                    position = Positions.MO3;
                    break;
                case 3:
                    position = Positions.MO2;
                    break;
                case 0:
                    position = Positions.FW3;
                    break;

            }
        }
        return position;
    }
    private void onPositionSelected(int index)
    {
        // Reset all buttons to default (white)
        for (FloatingActionButton button : positionButtons)
        {
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white)));
        }

        // Highlight the selected button with light green
        positionButtons[index].setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightGreen)));

        // Set selected position
        selectedPosition = index;
    }
}