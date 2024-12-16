// Updated MatchApplication6x6 to reflect changes for 6x6 matches
package com.example.sportscompiler;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportscompiler.AdditionalClasses.Application;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.Player;
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
    private FloatingActionButton kaleciA, ortadefansA, solortaA, sagortaA, ortasahaA, ortaforvetA,
            kaleciB, ortadefansB, solortaB, sagortaB, ortasahaB, ortaforvetB;
    private Button applyButton;
    private EditText applicatonNote;
    private String matchID, matchType, applicationNoteStr;
    private Match match;
    private Positions positionToApply;
    private TeamType teamToApply;
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

        // Initialize FloatingActionButtons for TEAM_A
        kaleciA = findViewById(R.id.kaleciA);
        ortadefansA = findViewById(R.id.ortadefansA);
        solortaA = findViewById(R.id.solKanatA);
        sagortaA = findViewById(R.id.sagKanatA);
        ortasahaA = findViewById(R.id.ortasahaA);
        ortaforvetA = findViewById(R.id.ortaforvetA);

        // Initialize FloatingActionButtons for TEAM_B
        kaleciB = findViewById(R.id.kaleciB);
        ortadefansB = findViewById(R.id.ortadefansB);
        solortaB = findViewById(R.id.solkanatB);
        sagortaB = findViewById(R.id.sagkanatB);
        ortasahaB = findViewById(R.id.ortasahaB);
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
                if (error != null) {
                    Toast.makeText(MatchApplication6x6.this, "Could not access database", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value != null && value.exists()) {
                    match = value.toObject(Match.class);
                } else {
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
                if (match != null) {
                    if (positionToApply == null) {
                        Toast.makeText(MatchApplication6x6.this, "Choose position", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    applyForPosition();
                }
            }
        });
    }

    private class FABListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == kaleciA.getId()) {
                updateFABButtonColor(kaleciA, Positions.GK1, TeamType.TEAM_A);
            } else if (view.getId() == kaleciB.getId()) {
                updateFABButtonColor(kaleciB, Positions.GK1, TeamType.TEAM_B);
            } else if (view.getId() == ortadefansB.getId()) {
                updateFABButtonColor(ortadefansB, Positions.CB3, TeamType.TEAM_B);
            } else if (view.getId() == ortadefansA.getId()) {
                updateFABButtonColor(ortadefansA, Positions.CB3, TeamType.TEAM_A);
            } else if (view.getId() == solortaB.getId()) {
                updateFABButtonColor(solortaB, Positions.MO1, TeamType.TEAM_B);
            } else if (view.getId() == sagortaB.getId()) {
                updateFABButtonColor(sagortaB, Positions.MO2, TeamType.TEAM_B);
            } else if (view.getId() == sagortaA.getId()) {
                updateFABButtonColor(sagortaA, Positions.MO2, TeamType.TEAM_A);
            } else if (view.getId() == solortaA.getId()) {
                updateFABButtonColor(solortaA, Positions.MO1, TeamType.TEAM_A);
            } else if (view.getId() == ortaforvetB.getId()) {
                updateFABButtonColor(ortaforvetB, Positions.FW3, TeamType.TEAM_B);
            } else if (view.getId() == ortaforvetA.getId()) {
                updateFABButtonColor(ortaforvetA, Positions.FW3, TeamType.TEAM_A);
            } else if (view.getId() == ortasahaA.getId()) {
                updateFABButtonColor(ortasahaA, Positions.MO3, TeamType.TEAM_A);
            } else if (view.getId() == ortasahaB.getId()) {
                updateFABButtonColor(ortasahaB, Positions.MO3, TeamType.TEAM_B);
            }
        }
    }

    private void updateFABButtonColor(FloatingActionButton button, Positions position, TeamType team) {
        firestore.collection(matchType).document(matchID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Match match = documentSnapshot.toObject(Match.class);
                Player playerAtPos;
                if (team == TeamType.TEAM_A) {
                    playerAtPos = match.getPlayersA().get(position.getAction());
                } else {
                    playerAtPos = match.getPlayersB().get(position.getAction());
                }

                if (playerAtPos != null) {
                    // Position is filled
                    button.setBackgroundColor(Color.GRAY);  // Change color to indicate position is taken
                    showPlayerDetailsDialog(playerAtPos);  // Show player details in a dialog
                } else {
                    // Position is empty
                    positionToApply = position;
                    teamToApply = team;
                    button.setBackgroundColor(Color.parseColor("#6200EE"));  // Default color (e.g., purple)
                }
            }
        });
    }

    private void showPlayerDetailsDialog(Player player) {
        Dialog dialog = new Dialog(MatchApplication6x6.this);
        dialog.setContentView(R.layout.dialog_user_details);

        ImageView profileImage = dialog.findViewById(R.id.profileImage);
        TextView nameText = dialog.findViewById(R.id.nameText);
        TextView ratingText = dialog.findViewById(R.id.ratingText);

        nameText.setText(player.getName());
        ratingText.setText("Rating: " + player.getRating());
        // Fetch and load the profile picture from Firestore
        firestore.collection("users").document(player.getUserID()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("profilePicture")) {
                        String uri = documentSnapshot.getString("profilePicture");


                        Bitmap bitmap = fireUser.decodeBase64ToImage(uri);


                        Uri imageUri = fireUser.saveBitmapToFile(MatchApplication6x6.this, bitmap);
                        profileImage.setImageURI(imageUri);

                    } else {
                        profileImage.setImageResource(R.drawable.blank_profile_picture);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MatchApplication6x6.this, "Failed to load profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    profileImage.setImageResource(R.drawable.blank_profile_picture);
                });


        dialog.show();
    }

    private void applyForPosition() {
        applicationNoteStr = applicatonNote.getText().toString();
        Application newApplication = new Application(user.getName(), positionToApply, applicationNoteStr, teamToApply, user.getUserID(), user.getAverageRating());
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
