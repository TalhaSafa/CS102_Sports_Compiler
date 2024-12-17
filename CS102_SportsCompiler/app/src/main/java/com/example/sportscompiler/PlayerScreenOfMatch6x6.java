package com.example.sportscompiler;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
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
import com.example.sportscompiler.AdditionalClasses.Player;
import com.example.sportscompiler.AdditionalClasses.Positions;
import com.example.sportscompiler.AdditionalClasses.RatingCallback;
import com.example.sportscompiler.AdditionalClasses.SearchForPlayer;
import com.example.sportscompiler.AdditionalClasses.TeamType;
import com.example.sportscompiler.AdditionalClasses.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlayerScreenOfMatch6x6 extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private Match currentMatch;
    private Button confirmationButton, reportAdminButton;
    private FloatingActionButton[] positionButtons;
    private EditText matchScoreForTeamA, matchScoreForTeamB;
    private String matchID;
    private String matchType;
    private ImageView fieldImage;
    private TextView enterMatchScore;
    private int selectedPosition = -1;
    private String currentUserID, playerName, playerID;
    private double currentRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_screen_of_match6x6);
        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");
        confirmationButton = findViewById(R.id.confirmationButton);
        positionButtons = new FloatingActionButton[]{
                findViewById(R.id.fab_player1),
                findViewById(R.id.fab_player2),
                findViewById(R.id.fab_player3),
                findViewById(R.id.fab_player4),
                findViewById(R.id.fab_player5),
                findViewById(R.id.fab_player6),
        };
        matchScoreForTeamA = findViewById(R.id.matchScoreForTeamA);
        matchScoreForTeamB = findViewById(R.id.matchScoreForTeamB);

        fieldImage = findViewById(R.id.footballField);
        enterMatchScore = findViewById(R.id.enterMatchScore);
        firestore = FirebaseFirestore.getInstance();

        reportAdminButton = findViewById(R.id.reportAdminButton);

        firestore.collection(matchType).document(matchID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot != null) {
                currentMatch = documentSnapshot.toObject(Match.class);
                setVisibilites();

                for(int i = 0; i < positionButtons.length; i++)
                {
                    int index = i;
                    Positions playerPosition = determinePosition(index, 6);
                    TeamType teamType = SearchForPlayer.returnTeamType(currentUserID, currentMatch);

                    if(currentMatch == null || getPlayerIdInClickedPosition(playerPosition, teamType) == null)
                    {
                        positionButtons[i].setEnabled(false);
                    }

                    positionButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            onPositionSelected(index);
                            Positions playerPosition = determinePosition(index, 6);
                            playerID = getPlayerIdInClickedPosition(playerPosition, SearchForPlayer.returnTeamType(currentUserID, currentMatch));

                            if(playerID != null)
                            {
                                positionButtons[index].setEnabled(true);
                                playerName = getPlayerNameInClickedPosition(playerPosition, SearchForPlayer.returnTeamType(currentUserID, currentMatch));
                                getPlayerRatingInClickedPosition(playerPosition, SearchForPlayer.returnTeamType(currentUserID, currentMatch));
                            }
                            else
                            {
                                positionButtons[index].setEnabled(false);
                            }
                        }
                    });
                }
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
                    Toast.makeText(PlayerScreenOfMatch6x6.this, "Enter Match Score", Toast.LENGTH_SHORT).show();

                }
            }
        });

        reportAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

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
                    Toast.makeText(PlayerScreenOfMatch6x6.this, "Match Score Updated!", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PlayerScreenOfMatch6x6.this, "Could Not Update Match Score!", Toast.LENGTH_SHORT).show();
                    currentMatch.setTeamAScore(0);
                    currentMatch.setTeamBScore(0);
                }
            });
        }
    }

    private void getPlayerRatingInClickedPosition(Positions position, TeamType team)
    {
        if(team == TeamType.TEAM_A)
        {
            if(currentMatch.getPlayersA() != null)
            {
                currentMatch.getPlayersA().get(position.getAction()).getRating(new RatingCallback() {
                    @Override
                    public void onRatingFetched(double rating1) {
                        currentRating = rating1;
                        RatingDialogFragment ratingDialogFragment = RatingDialogFragment.newInstance(playerName, currentRating, playerID);
                        ratingDialogFragment.show(getSupportFragmentManager().beginTransaction(), "Rating Dialog");
                    }
                });
            }
        }
        else
        {
            if(currentMatch.getPlayersB() != null)
            {
                currentMatch.getPlayersB().get(position.getAction()).getRating(new RatingCallback() {
                    @Override
                    public void onRatingFetched(double rating1) {
                        currentRating = rating1;
                        RatingDialogFragment ratingDialogFragment = RatingDialogFragment.newInstance(playerName, currentRating, playerID);
                        ratingDialogFragment.show(getSupportFragmentManager().beginTransaction(), "Rating Dialog");
                    }
                });
            }
        }

    }

    private String getPlayerIdInClickedPosition(Positions position, TeamType team)
    {
        String wantedPlayerID = null;

        if(team == TeamType.TEAM_A)
        {
            if(currentMatch.getPlayersA() != null)
            {
                Player player = currentMatch.getPlayersA().get(position.getAction());

                if(player != null && player.getUserID() != null)
                {
                    wantedPlayerID = player.getUserID();
                }
            }
        }
        else
        {
            if(currentMatch.getPlayersB() != null)
            {
                Player player = currentMatch.getPlayersB().get(position.getAction());

                if(player != null && player.getUserID() != null)
                {
                    wantedPlayerID = currentMatch.getPlayersB().get(position.getAction()).getUserID();
                }
            }
        }

        if(wantedPlayerID == null)
        {
            Log.e("PlayerScreen", "Player not found for position: " + position);
            
        }
        return wantedPlayerID;
    }

    private Positions determinePosition(int selectedPosition , int numberOfPlayersInATeam)
    {
        Positions position = Positions.GK1;
        if(numberOfPlayersInATeam == 6)
        {
            switch (selectedPosition)
            {
                case 4:
                    position = Positions.MO3;
                    break;
                case 2:
                    position = Positions.FW3;
                    break;
                case 1:
                    position = Positions.MO2;
                    break;
                case 3:
                    position = Positions.GK1;
                    break;
                case 0:
                    position = Positions.MO1;
                    break;
                case 5:
                    position = Positions.CB3;
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

    private String getPlayerNameInClickedPosition(Positions position, TeamType team)
    {
        String playerName = null;

        if(team == TeamType.TEAM_A)
        {
            if(currentMatch.getPlayersA() != null)
            {
                playerName = currentMatch.getPlayersA().get(position.getAction()).getName();
            }
        }
        else
        {
            if(currentMatch.getPlayersB() != null)
            {
                playerName = currentMatch.getPlayersB().get(position.getAction()).getName();
            }
        }
        return playerName;
    }
}