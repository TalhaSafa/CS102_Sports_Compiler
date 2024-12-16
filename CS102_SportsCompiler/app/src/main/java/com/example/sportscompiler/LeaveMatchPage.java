package com.example.sportscompiler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportscompiler.AdditionalClasses.Match;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LeaveMatchPage extends AppCompatActivity {

    private Button matchForumButton;
    private String matchID, matchType;
    private FirebaseFirestore firestore;
    private Match currMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leave_match_page);

        matchForumButton = findViewById(R.id.ForumPage);


        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");

        firestore = FirebaseFirestore.getInstance();

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

    }
}