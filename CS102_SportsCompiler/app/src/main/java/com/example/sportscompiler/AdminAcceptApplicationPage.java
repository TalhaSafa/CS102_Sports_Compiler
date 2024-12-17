package com.example.sportscompiler;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.example.sportscompiler.AdditionalClasses.DismissActionListener;
import com.example.sportscompiler.AdditionalClasses.DismissAdapter;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.Message;
import com.example.sportscompiler.AdditionalClasses.Player;
import com.example.sportscompiler.AdditionalClasses.PlayerAdapter;
import com.example.sportscompiler.AdditionalClasses.SearchForPlayer;
import com.example.sportscompiler.AdditionalClasses.TeamType;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminAcceptApplicationPage extends AppCompatActivity {

    private Button matchForumButton, cancelButton;
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
    private TextView matchName, matchDate, matchAdminName, matchNote;
    private RecyclerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_accept_application_page);

        playerView = findViewById(R.id.playersRecyclerView);
        playerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView = findViewById(R.id.applicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        matchForumButton = findViewById(R.id.ForumPage);
        matchName = findViewById(R.id.matchName);
        matchDate = findViewById(R.id.matchDate);
        matchAdminName = findViewById(R.id.adminName);
        matchNote = findViewById(R.id.matchNote);
        cancelButton = findViewById(R.id.cancelMatch);


        fireuser = new firestoreUser();
        user = new User();
        firestore = FirebaseFirestore.getInstance();
        initializeUser();

        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");

        fetchMatchFromFirestore(matchID, matchType);


        cancelButton.setOnClickListener(new CancelListener());
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
                    matchName.setText(currentMatch.getMatchName());
                    matchDate.setText(currentMatch.getDate().toDate().toString());
                    matchAdminName.setText(currentMatch.getAdminName());
                    matchNote.setText(currentMatch.getNotes());
                    fillApplications();
                    fillPlayers();// Update the applications list in real-time
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

                            if(application.getTeamInfo() == TeamType.TEAM_A)
                            {
                                addToTeam(currentMatch.getPlayersA(), application);

                            }
                            else
                            {
                                addToTeam(currentMatch.getPlayersB(), application);


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
    private void fillPlayers() {
        if (currentMatch == null) {
            Log.e("fillPlayers", "currentMatch is null, cannot populate players.");
            return;
        }

        // Create a list to hold player objects
        List<Player> playerList = new ArrayList<>();

        // Extract players from Team A
        if (currentMatch.getPlayersA() != null) {
            for (Player player : currentMatch.getPlayersA().values()) {
                if (player != null) { // Check if the Player object itself is not null
                    String playerName = player.getName() != null ? player.getName() : "Unknown Player";
                    String position = (player.getPosition() != null && player.getPosition().getAction() != null)
                            ? player.getPosition().getAction()
                            : "Unknown Position";
                    playerList.add(player); // Add Player object
                }
            }
        }

        // Extract players from Team B
        if (currentMatch.getPlayersB() != null) {
            for (Player player : currentMatch.getPlayersB().values()) {
                if (player != null) { // Check if the Player object itself is not null
                    String playerName = player.getName() != null ? player.getName() : "Unknown Player";
                    String position = (player.getPosition() != null && player.getPosition().getAction() != null)
                            ? player.getPosition().getAction()
                            : "Unknown Position";
                    playerList.add(player); // Add Player object
                }
            }
        }

        // Set up the adapter with the player list
        DismissAdapter playerAdapter = new DismissAdapter(playerList, new DismissActionListener() {
            @Override
            public void onDismissClick(Player player) {
                System.out.println(player);
                playerRemoveMatch(player);
            }
        });
        playerView.setAdapter(playerAdapter); // `playerView` is your RecyclerView
    }



    private void addToTeam(Map<String, Player> playersOfWantedTeam, Application application) {

        // Check if the position is already taken
        if (playersOfWantedTeam.get(application.getPosition().getAction()) != null) {
            Toast.makeText(AdminAcceptApplicationPage.this, "This position is already full!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the accepted Player
        Player acceptedPlayer = new Player(application.getUserID(), application.getAverage(),
                application.getTeamInfo(), application.getPosition(), false,
                currentMatch.getMatchID(), application.getName(),fireuser.getUserMail(application.getUserID()));

        // Check if the player is already in the match
        if (SearchForPlayer.doesMatchContainUser(currentMatch, acceptedPlayer.getUserID())) {
            Toast.makeText(AdminAcceptApplicationPage.this, "This player is already in this match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add player to Firestore
        firestore.collection("users").document(application.getUserID()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User userToAccept = documentSnapshot.toObject(User.class);
                    if (userToAccept == null) {
                        Toast.makeText(AdminAcceptApplicationPage.this, "User not found!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    userToAccept.addMatches(currentMatch.getMatchID());

                    // Save the updated user back to Firestore
                    firestore.collection("users").document(application.getUserID()).set(userToAccept)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Update local data structures
                                    playersOfWantedTeam.put(application.getPosition().getAction(), acceptedPlayer);
                                    currentMatch.getApplications().remove(application); // Sync with currentMatch
                                    applications.remove(application); // Update UI list

                                    // Save the updated match to Firestore
                                    firestore.collection(matchType).document(matchID).set(currentMatch)
                                            .addOnSuccessListener(aVoid -> {
                                                appAdapt.notifyDataSetChanged(); // Refresh UI
                                                Toast.makeText(AdminAcceptApplicationPage.this, "Application Accepted", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(AdminAcceptApplicationPage.this, "Failed to update match: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(AdminAcceptApplicationPage.this, "User update failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminAcceptApplicationPage.this, "User fetch failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void cancelMatch()
    {
        if(currentMatch != null)
        {
            firestore.collection(currentMatch.getMatchType()).document(currentMatch.getMatchID())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AdminAcceptApplicationPage.this, "Match has been deleted successfully.", Toast.LENGTH_SHORT).show();
                            // Navigate back or disable UI
                            FragmentLoad.changeActivity(AdminAcceptApplicationPage.this, homeActivity.class);
                            finish(); // Ends the current activity
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("deleteMatch", "Failed to delete match", e);
                            Toast.makeText(AdminAcceptApplicationPage.this, "Failed to delete the match. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
    private void playerRemoveMatch(Player player) {

        firestore.collection("users").document(player.getUserID()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        User user = documentSnapshot.toObject(User.class);

                        if (user != null) {

                            user.removeMatch(currentMatch.getMatchID());


                            firestore.collection("users").document(player.getUserID()).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        // Step 2: Modify the match object
                                        currentMatch.removePlayer(currentMatch,player.getTeam(), player.getPosition());

                                        firestore.collection(currentMatch.getMatchType()).document(currentMatch.getMatchID())
                                                .set(currentMatch)
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(AdminAcceptApplicationPage.this, "Player removed successfully!", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    currentMatch.addPlayer(player.getTeam(), player);
                                                    Toast.makeText(AdminAcceptApplicationPage.this, "Failed to update match: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AdminAcceptApplicationPage.this, "Failed to update user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(AdminAcceptApplicationPage.this, "User object is null!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AdminAcceptApplicationPage.this, "User does not exist in Firestore!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminAcceptApplicationPage.this, "Could not access user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private class CancelListener implements View.OnClickListener
    {

        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(AdminAcceptApplicationPage.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to cancel this match?")
                    .setPositiveButton("Yes", (dialog, which) -> cancelMatch())
                    .setNegativeButton("No", null)
                    .show();
        }
        }

}