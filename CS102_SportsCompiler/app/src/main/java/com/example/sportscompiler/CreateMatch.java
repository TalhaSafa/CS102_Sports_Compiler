package com.example.sportscompiler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class CreateMatch extends AppCompatActivity {

    private EditText matchNameEditText, notesEditText;
    private Spinner citySpinner, personCountSpinner;
    private Button createButton, dateButton, timeButton;

    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_match);

        matchNameEditText = findViewById(R.id.editTextText);
        notesEditText = findViewById(R.id.notes_edit_text);
        citySpinner = findViewById(R.id.city_spinner);
        personCountSpinner = findViewById(R.id.personCount);
        createButton = findViewById(R.id.button);
        dateButton = findViewById(R.id.date_button);
        timeButton = findViewById(R.id.time_button);

        selectedDateTime = Calendar.getInstance();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateMatch.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth)
                    {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateButton.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                        selectedDateTime.get(Calendar.YEAR),
                        selectedDateTime.get(Calendar.MONTH),
                        selectedDateTime.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        CreateMatch.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute)
                    {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);

                        String minuteText;

                        if(minute < 10)
                        {
                            minuteText = "0" + minute;
                        }
                        else
                        {
                            minuteText = String.valueOf(minute);
                        }

                        timeButton.setText(hourOfDay + ":" + minuteText);
                    }
                },
                        selectedDateTime.get(Calendar.HOUR_OF_DAY),
                        selectedDateTime.get(Calendar.MINUTE),
                        true
                );
                timePickerDialog.show();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                String matchName = matchNameEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                long currentTimeMillis = System.currentTimeMillis();

                String city;
                if(citySpinner.getSelectedItem() != null)
                {
                    city = citySpinner.getSelectedItem().toString();
                }
                else
                {
                    city = "";
                }

                String personCount;
                if(personCountSpinner.getSelectedItem() != null)
                {
                    personCount = personCountSpinner.getSelectedItem().toString();
                }
                else
                {
                    personCount = "";
                }

                if(matchName.isEmpty() || matchName.equals("Name of Match"))
                {
                    Toast.makeText(CreateMatch.this, "Please enter a match name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(city.isEmpty() || city.equals("Select a Place"))
                {
                    Toast.makeText(CreateMatch.this, "Please enter a place", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(personCount.isEmpty() || personCount.equals("Select person count"))
                {
                    Toast.makeText(CreateMatch.this, "Please enter a quota", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(selectedDateTime.getTimeInMillis() <= currentTimeMillis)
                {
                    Toast.makeText(CreateMatch.this, "Please enter a proper date and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Example: Display the data
                Intent intent = new Intent(CreateMatch.this, AdminPositionSelector.class);
                intent.putExtra("matchName", matchName);
                intent.putExtra("notes", notes);
                intent.putExtra("city", city);
                intent.putExtra("personCount", personCount);
                intent.putExtra("dateTime", selectedDateTime.getTimeInMillis());
                startActivity(intent);
            }
        });

    }
}