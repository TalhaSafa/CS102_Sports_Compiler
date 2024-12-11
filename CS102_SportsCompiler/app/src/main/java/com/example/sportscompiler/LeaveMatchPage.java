package com.example.sportscompiler;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LeaveMatchPage extends AppCompatActivity {

    private Button matchForumButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leave_match_page);
        matchForumButton = findViewById(R.id.ForumPage);

        matchForumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FragmentLoad.changeActivity(LeaveMatchPage.this, MatchForumActivity.class);
            }
        });

    }
}