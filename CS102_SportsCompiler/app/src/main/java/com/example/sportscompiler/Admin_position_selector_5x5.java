package com.example.sportscompiler;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
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
import androidx.core.content.ContextCompat;

import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.MatchFields;
import com.example.sportscompiler.AdditionalClasses.Message;
import com.example.sportscompiler.AdditionalClasses.Player;
import com.example.sportscompiler.AdditionalClasses.Positions;
import com.example.sportscompiler.AdditionalClasses.TeamType;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Admin_position_selector_5x5 extends AppCompatActivity {

    private FloatingActionButton[] positionButtons;
    private Button createMatchButton;
    private int selectedPosition = -1;
    private firestoreUser fireuser;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String matchName, notes, city, personCount;
    private long dateTimeMillis;
    private Match newMatch;
    private String matchID;
    private MatchFields matchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_position_selector5x5);

        firebaseAuth = FirebaseAuth.getInstance();
        fireuser = new firestoreUser();
        user = new User();
        initializeUser();

        positionButtons = new FloatingActionButton[]{
                findViewById(R.id.position_1),
                findViewById(R.id.position_2),
                findViewById(R.id.position_3),
                findViewById(R.id.position_4),
                findViewById(R.id.position_5)
        };
        createMatchButton = findViewById(R.id.createMatchFinal);
        createMatchButton.setEnabled(false);

        matchName = getIntent().getStringExtra("matchName");
        notes = getIntent().getStringExtra("notes");
        city = getIntent().getStringExtra("city");
        personCount = getIntent().getStringExtra("personCount");
        dateTimeMillis = getIntent().getLongExtra("dateTime", -1);

        for (int i = 0; i < positionButtons.length; i++) {
            final int index = i;

            positionButtons[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onPositionSelected(index);
                }
            });
        }

        createMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition != -1) {
                    int numberOfPlayersInATeam = Integer.parseInt(personCount) / 2;
                    Positions adminPosition = determinePosition(selectedPosition, numberOfPlayersInATeam);
                    long seconds = dateTimeMillis / 1000;
                    int nanoseconds = (int) ((dateTimeMillis % 1000) * 1000000);
                    Timestamp date = new Timestamp(seconds, nanoseconds);

                    if(city.equals("Main Campus 1"))
                    {
                        matchField = MatchFields.MAIN1;
                    }
                    else if(city.equals("Main Campus 2"))
                    {
                        matchField = MatchFields.MAIN2;
                    }
                    else if(city.equals("East Campus"))
                    {
                        matchField = MatchFields.EAST;
                    }

                    createNewMatch(user.getUserID(), matchName, numberOfPlayersInATeam, adminPosition, date, view);
                    FragmentLoad.changeActivity(Admin_position_selector_5x5.this, homeActivity.class);// "Create Match" butonunu görünmez yap
                    finish();

                }
            }
        });
    }

        //TODO need to be changed when added other position distribution:
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
                    position = Positions.MO1;
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

        // Enable the Create button
        createMatchButton.setEnabled(true);
    }

    //To initialize team maps
    private void initializeMaps(Map<String, Player> team, int numberOfPlayersInATeam)
    {
        if(numberOfPlayersInATeam == 5)
        {
            team.put(Positions.GK1.getAction(), null);
            team.put(Positions.CB3.getAction(), null);
            team.put(Positions.MO1.getAction(), null);
            team.put(Positions.MO2.getAction(), null);
            team.put(Positions.FW3.getAction(), null);
        }

        if(numberOfPlayersInATeam == 6)
        {
            team.put(Positions.GK1.getAction(), null);
            team.put(Positions.CB1.getAction(), null);
            team.put(Positions.CB2.getAction(), null);
            team.put(Positions.MO1.getAction(), null);
            team.put(Positions.MO2.getAction(), null);
            team.put(Positions.FW3.getAction(), null);
        }
    }

    private void setAdminPosition(Map<String, Player> team, Positions adminPosition, String matchID)
    {
        Player admin = new Player(user.getUserID(), user.getAverageRating(), TeamType.TEAM_A, adminPosition, true, matchID, user.getName(),user.getEmail());
        team.put(adminPosition.getAction(), admin);
    }

    //This method is to try whether database is working or not:
    private void createNewMatch(String adminID, String matchName, int numberOfPlayersInATeam, Positions adminPosition, Timestamp date, View view){
        Map<String, Player> teamA = new HashMap<>();
        Map<String, Player> teamB = new HashMap<>();

        initializeMaps(teamA, numberOfPlayersInATeam);
        initializeMaps(teamB, numberOfPlayersInATeam);

        //To create distinct id for each match:
        matchID = adminID + date.toDate().toString();

        setAdminPosition(teamA, adminPosition, matchID);
        String adminName = user.getName();

        newMatch = new Match(adminID, adminName, matchName, date, matchField, teamA, teamB, adminPosition.getAction(), notes, matchID, 0 , 0);

        firestore = FirebaseFirestore.getInstance();

        firestore.collection(newMatch.getMatchType()).document(matchID).set(newMatch)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(view.getContext(), "Created new match", Toast.LENGTH_SHORT).show();
                            user.addMatches(newMatch.getMatchID());
                            firestore.collection("users").document(user.getUserID()).set(user);

                        }
                        else
                        {
                            Toast.makeText(view.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeForum(View view)
    {
        Timestamp nowTime = Timestamp.now();
        String messageID = newMatch.getMatchID() + user.getName() + nowTime.toDate().toString();
        Message startingMessage = new Message(user, nowTime, user.getName() + " created this match."
                , messageID);

        firestore.collection(newMatch.getMatchType()).document(matchID).collection("forum")
                .document("SystemMessage").set(startingMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!(task.isSuccessful()))
                        {
                            Toast.makeText(view.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
