package com.example.sportscompiler;

import android.app.Activity;
import android.content.Intent;
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

import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.Message;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAcceptApplicationPage extends AppCompatActivity {

    private Button matchForumButton;
    private firestoreUser fireuser;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Match currentMatch;
    private String matchID;
    private Timestamp date;
    private long dateTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_accept_application_page);
        matchForumButton = findViewById(R.id.ForumPage);

        firebaseAuth = FirebaseAuth.getInstance();
        fireuser = new firestoreUser();
        user = new User();
        firestore = FirebaseFirestore.getInstance();
        initializeUser();

        matchID = getIntent().getStringExtra("matchID");
        dateTimeMillis = getIntent().getLongExtra("dateTime", -1);


        long seconds = dateTimeMillis / 1000;
        int nanoseconds = (int) ((dateTimeMillis % 1000) * 1000000);
        date = new Timestamp(seconds, nanoseconds);

        matchForumButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                initializeForum(date, view);
                Intent intent = new Intent(AdminAcceptApplicationPage.this, MatchForumActivity.class);
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

    private void initializeForum(Timestamp date, View view)
    {
        String messageID = matchID +  user.getName() + date.toDate().toString();
        Message startingMessage = new Message(user, date, user.getName() + " created this match."
                , messageID);

        firestore.collection(currentMatch.getMatchType()).document(matchID).collection("forum")
                .document("SystemMessage").set(startingMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!(task.isSuccessful()))
                        {
                            Toast.makeText(view.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            FragmentLoad.changeActivity((Activity) view.getContext(), MatchForumActivity.class);
                        }
                    }
                });
    }

}