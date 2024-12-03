package com.example.sportscompiler;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminPositionSelector extends AppCompatActivity {

    private FloatingActionButton[] positionButtons;
    private Button createMatchButton;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_position_selector);

        positionButtons = new FloatingActionButton[]{
                findViewById(R.id.position_1),
                findViewById(R.id.position_2),
                findViewById(R.id.position_3),
                findViewById(R.id.position_4),
                findViewById(R.id.position_5),
                findViewById(R.id.position_6)
        };
        createMatchButton = findViewById(R.id.createMatchFinal);
        createMatchButton.setEnabled(false);

        String matchName = getIntent().getStringExtra("matchName");
        String notes = getIntent().getStringExtra("notes");
        String city = getIntent().getStringExtra("city");
        String personCount = getIntent().getStringExtra("personCount");
        long dateTimeMillis = getIntent().getLongExtra("dateTime", -1);

        for(int i = 0; i < positionButtons.length; i++)
        {
            final int index = i;

            positionButtons[i].setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    onPositionSelected(index);
                }
            });
        }

        createMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(selectedPosition != -1)
                {
                    Toast.makeText(AdminPositionSelector.this, "Position " + (selectedPosition + 1) + " selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onPositionSelected(int index)
    {
        for (FloatingActionButton button : positionButtons)
        {
            button.setBackgroundTintList(getColorStateList(android.R.color.white));
        }

        // Highlight the selected button
        positionButtons[index].setBackgroundTintList(getColorStateList(android.R.color.holo_green_light));

        // Set selected position
        selectedPosition = index;

        // Enable the Create button
        createMatchButton.setEnabled(true);
    }
}