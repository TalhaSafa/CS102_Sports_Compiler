package com.example.sportscompiler;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MatchApplication5x5 extends AppCompatActivity {

    private FloatingActionButton kaleciA, ortadefansA, solortaA, sagortaA, ortaforvetA,
            kaleciB, ortadefansB, solortaB, sagortaB, ortaforvetB;
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
    private FloatingActionButton selectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_match_application5x5);

        fireUser = new firestoreUser();
        firebaseAuth = FirebaseAuth.getInstance();

        applicationNoteStr = "";
        applyButton = findViewById(R.id.applyButton);
        applicatonNote = findViewById(R.id.applicationNote);
        user = new User();
        initializeUser();

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
                if (error != null) {
                    Toast.makeText(MatchApplication5x5.this, "Could not access database", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value != null && value.exists()) {
                    match = value.toObject(Match.class);
                    markFilledPositions();
                    matchName.setText("Match Name: " + match.getMatchName());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    String dateStr = dateFormat.format(match.getDate().toDate());
                    dateTxt.setText("Match Date: " + dateStr);
                    adminName.setText("Admin: " + match.getAdminName());
                    matchNote.setText("Match Note: " + match.getNotes());
                } else {
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
                if (match != null) {
                    if (positionToApply == null) {
                        Toast.makeText(MatchApplication5x5.this, "Choose position", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        applyForPosition();
                        FragmentLoad.changeActivity(MatchApplication5x5.this, homeActivity.class);// "Create Match" butonunu görünmez yap
                        finish();
                    }

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
                    // Pozisyon dolu
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff2e2e")));
                    showPlayerDetailsDialog(playerAtPos);
                } else {
                    // Pozisyon boş
                    positionToApply = position;
                    teamToApply = team;

                    if (selectedButton != null) {
                        // Önceki seçimin rengini sıfırla
                        selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF"))); // Varsayılan renk
                    }

                    // Yeni seçimi vurgula
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#90EE90"))); // Vurgulama rengi
                    selectedButton = button; // Yeni seçilen butonu güncelle
                }
            }
        });
    }

    private void markFilledPositions() {
        firestore.collection(matchType).document(matchID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Match match = documentSnapshot.toObject(Match.class);

                if (match != null) {
                    updateButtonState(kaleciA, Positions.GK1, TeamType.TEAM_A, match);
                    updateButtonState(kaleciB, Positions.GK1, TeamType.TEAM_B, match);
                    updateButtonState(ortadefansA, Positions.CB3, TeamType.TEAM_A, match);
                    updateButtonState(ortadefansB, Positions.CB3, TeamType.TEAM_B, match);
                    updateButtonState(solortaA, Positions.MO1, TeamType.TEAM_A, match);
                    updateButtonState(solortaB, Positions.MO1, TeamType.TEAM_B, match);
                    updateButtonState(sagortaA, Positions.MO2, TeamType.TEAM_A, match);
                    updateButtonState(sagortaB, Positions.MO2, TeamType.TEAM_B, match);
                    updateButtonState(ortaforvetA, Positions.FW3, TeamType.TEAM_A, match);
                    updateButtonState(ortaforvetB, Positions.FW3, TeamType.TEAM_B, match);
                }
            }
        });
    }

    private void updateButtonState(FloatingActionButton button, Positions position, TeamType team, Match match) {
        Player playerAtPos = (team == TeamType.TEAM_A) ? match.getPlayersA().get(position.getAction()) : match.getPlayersB().get(position.getAction());

        if (playerAtPos != null) {

            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff2e2e")));

        }
        else{
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
    }



    private void showPlayerDetailsDialog(Player player) {

        Dialog dialog = new Dialog(MatchApplication5x5.this);
        dialog.setContentView(R.layout.dialog_user_details);

        ImageView profileImage = dialog.findViewById(R.id.profileImage);
        TextView nameText = dialog.findViewById(R.id.nameText);
        TextView ratingText = dialog.findViewById(R.id.ratingText);

        // Fetch and load the profile picture from Firestore
        firestore.collection("users").document(player.getUserID()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("profilePicture")) {
                        String uri = documentSnapshot.getString("profilePicture");


                        Bitmap bitmap = fireUser.decodeBase64ToImage(uri);


                        Uri imageUri = fireUser.saveBitmapToFile(MatchApplication5x5.this, bitmap);
                        profileImage.setImageURI(imageUri);


                    } else {
                        profileImage.setImageResource(R.drawable.blank_profile_picture);
                    }

                    if (documentSnapshot.exists() && documentSnapshot.contains("name")) {

                        nameText.setText("Name: " + documentSnapshot.getString("name"));

                    }
                    else{
                        nameText.setText("null");
                    }
                    if (documentSnapshot.exists() && documentSnapshot.contains("averageRating")) {
                        ratingText.setText("Rating: " + Double.toString(documentSnapshot.getDouble("averageRating")));

                    }
                    else{
                        ratingText.setText("null");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MatchApplication5x5.this, "Failed to load profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    profileImage.setImageResource(R.drawable.blank_profile_picture); // Default picture
                });

        dialog.show();
    }


    private void applyForPosition() {
        // Eğer pozisyon boşsa başvuru yapılacaksa, bu işlemi burada gerçekleştirebilirsiniz.

        applicationNoteStr = applicatonNote.getText().toString();
        Application newApplication = new Application(user.getName(), positionToApply, calculateAge(user.getBirthDate()), user.getDepartment(),  applicationNoteStr, teamToApply, user.getUserID(), user.getAverageRating());
        match.addApplication(newApplication);
        firestore.collection(matchType).document(matchID).set(match).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MatchApplication5x5.this, "Application sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MatchApplication5x5.this, "Match not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int calculateAge(String birthday) {
        // Define the date format for "dd/mm/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int age;
        try {
            // Parse the birthday string into a Date object
            Date birthDate = dateFormat.parse(birthday);

            // Get the current date
            Calendar today = Calendar.getInstance();

            // Set the calendar to the birth date
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(birthDate);

            // Calculate the age
            age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            // If the birthdate hasn't occurred yet this year, subtract one
            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

        }
        catch (ParseException e)
        {
            age = 0;
        }
        return age;
    }

    private void initializeUser() {
        applyButton.setEnabled(false);

        fireUser.updateInfo(user, new firestoreUser.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                user = result;
                Log.d("initializeUser", "User info successfully retrieved: " + user.getName());
                applyButton.setEnabled(true);
            }

            @Override
            public void onError(Exception e) {
                Log.e("MatchApplication", "Error fetching user info", e);
                Toast.makeText(MatchApplication5x5.this, "Failed to fetch user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserProfilePicture(String userId, FloatingActionButton button) {
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("profilePicture")) {
                        String base64Image = documentSnapshot.getString("profilePicture");
                        Bitmap bitmap = fireUser.decodeBase64ToImage(base64Image);
                        Uri imageUri = fireUser.saveBitmapToFile(this,bitmap);

                        if (bitmap != null) {
                                 button.setImageTintList(null); // Remove tint
                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                button.setImageDrawable(drawable);



                        } else {
                            button.setImageResource(R.drawable.blank_profile_picture); // Default icon if decoding fails
                        }
                    } else {
                        button.setImageResource(R.drawable.blank_profile_picture); // Default icon if no profile picture exists
                    }
                })
                .addOnFailureListener(e -> {
                    button.setImageResource(R.drawable.blank_profile_picture); // Default icon on failure
                    Log.e("fetchUserProfilePicture", "Error fetching user profile picture", e);
                });
    }
}
