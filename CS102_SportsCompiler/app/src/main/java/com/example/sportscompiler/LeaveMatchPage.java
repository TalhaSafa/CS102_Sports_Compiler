package com.example.sportscompiler;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.Player;
import com.example.sportscompiler.AdditionalClasses.SearchForPlayer;
import com.example.sportscompiler.AdditionalClasses.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LeaveMatchPage extends AppCompatActivity {

    private Button matchForumButton, leaveButton;
    private String matchID, matchType;
    private FirebaseFirestore firestore;
    private Match currMatch;
    private FirebaseAuth fAuth;
    private Player currPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leave_match_page);

        matchForumButton = findViewById(R.id.ForumPage);
        leaveButton = findViewById(R.id.buttonLeave);

        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");

        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        firestore.collection(matchType).document(matchID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(LeaveMatchPage.this, "Could not access database", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(value != null && value.exists())
                {
                    currMatch = value.toObject(Match.class);
                    currPlayer = SearchForPlayer.findPlayerWithID(currMatch, fAuth.getUid());
                    //TODO set informations related to match
                }
                else
                {
                    Toast.makeText(LeaveMatchPage.this, "Null Match", Toast.LENGTH_SHORT).show();
                }
            }
        });


        matchForumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LeaveMatchPage.this, MatchForumActivity.class);
                intent.putExtra("matchID", matchID);
                intent.putExtra("matchType", matchType);
                startActivity(intent);
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LeaveMatchPage.this)
                        .setTitle("Leave Match")
                        .setMessage("Are you sure you want to leave this match?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            leaveMatch(); // Call the leaveMatch method
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });


    }

    private void leaveMatch()
    {
        firestore.collection(currMatch.getMatchType()).document(currMatch.getMatchID()).set(currMatch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firestore.collection("users").document(currPlayer.getUserID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists() && documentSnapshot != null)
                        {
                            User user = documentSnapshot.toObject(User.class);
                            currMatch.removePlayer(currMatch, currPlayer.getTeam(), currPlayer.getPosition());
                            user.removeMatch(currMatch.getMatchID());

                            firestore.collection("users").document(user.getUserID()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FragmentLoad.changeActivity(LeaveMatchPage.this, homeActivity.class);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LeaveMatchPage.this, "Could not leave the match!", Toast.LENGTH_SHORT).show();
                                    currMatch.addPlayer(currPlayer.getTeam(), currPlayer);

                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LeaveMatchPage.this, "Could not access current user!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LeaveMatchPage.this, "Could not leave the match!", Toast.LENGTH_SHORT).show();
                currMatch.addPlayer(currPlayer.getTeam(), currPlayer);
            }
        });
    }
}