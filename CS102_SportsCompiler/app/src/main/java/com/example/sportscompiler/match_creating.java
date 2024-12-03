package com.example.sportscompiler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class match_creating extends Fragment {

    private EditText matchNameEditText, notesEditText;
    private Spinner citySpinner, personCountSpinner;
    private Button createButton, dateButton, timeButton;

    private Calendar selectedDateTime;

    public match_creating() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_creating, container, false);

        // Initialize views
        matchNameEditText = view.findViewById(R.id.editTextText);
        notesEditText = view.findViewById(R.id.notes_edit_text);
        citySpinner = view.findViewById(R.id.city_spinner);
        personCountSpinner = view.findViewById(R.id.personCount);
        createButton = view.findViewById(R.id.button);
        dateButton = view.findViewById(R.id.date_button);
        timeButton = view.findViewById(R.id.time_button);

        selectedDateTime = Calendar.getInstance();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), new DatePickerDialog.OnDateSetListener() {
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
                        getContext(), new TimePickerDialog.OnTimeSetListener() {
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


        // Set up click listener for the create button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                String matchName = matchNameEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                String city = citySpinner.getSelectedItem().toString();
                String personCount = personCountSpinner.getSelectedItem().toString();

                // Example: Display the data
                String matchDetails = "Match Name: " + matchName + "\n"
                        + "Date/Time: " + selectedDateTime.toString() + "\n"
                        + "City: " + city + "\n"
                        + "Person Count: " + personCount + "\n"
                        + "Notes: " + notes;

                // Show a Toast message (you can replace this with any logic, such as saving data)
                Toast.makeText(getActivity(), matchDetails, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
