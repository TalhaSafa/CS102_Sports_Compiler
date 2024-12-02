package com.example.sportscompiler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class match_creating extends Fragment {

    private EditText matchNameEditText, dateEditText, notesEditText;
    private Spinner citySpinner, personCountSpinner;
    private Button createButton;

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
        dateEditText = view.findViewById(R.id.editTextDate5);
        notesEditText = view.findViewById(R.id.notes_edit_text);
        citySpinner = view.findViewById(R.id.city_spinner);
        personCountSpinner = view.findViewById(R.id.personCount);
        createButton = view.findViewById(R.id.button);

        // Set up click listener for the create button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                String matchName = matchNameEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                String city = citySpinner.getSelectedItem().toString();
                String personCount = personCountSpinner.getSelectedItem().toString();

                // Example: Display the data
                String matchDetails = "Match Name: " + matchName + "\n"
                        + "Date/Time: " + date + "\n"
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
