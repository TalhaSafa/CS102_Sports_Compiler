package com.example.sportscompiler;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminPositionSelecter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminPositionSelecter extends Fragment {

    private FloatingActionButton[] positionButtons;
    private Button createMatchButton;
    private int selectedPosition = -1; // To track which position is selected

    public AdminPositionSelecter() {
        super(R.layout.fragment_admin_selectposition);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize position buttons
        positionButtons = new FloatingActionButton[]{
                view.findViewById(R.id.position_1),
                view.findViewById(R.id.position_2),
                view.findViewById(R.id.position_3),
                view.findViewById(R.id.position_4),
                view.findViewById(R.id.position_5),
                view.findViewById(R.id.position_6)
        };

        // Initialize the Create button
        createMatchButton = view.findViewById(R.id.createMatchFinal);
        createMatchButton.setEnabled(false); // Disable initially
        for (int i = 0; i < positionButtons.length; i++) {
            final int index = i; // Capture index for the listener
            positionButtons[i].setOnClickListener(v -> onPositionSelected(index));
        }

        createMatchButton.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                // Display the selected position
                Toast.makeText(getContext(), "Position " + (selectedPosition + 1) + " selected!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * Handles position selection.
     */
    private void onPositionSelected(int index) {
        // Reset colors for all buttons
        for (FloatingActionButton button : positionButtons) {
            button.setBackgroundTintList(requireContext().getColorStateList(android.R.color.white));
        }

        // Highlight the selected button
        positionButtons[index].setBackgroundTintList(requireContext().getColorStateList(android.R.color.holo_green_light));

        // Set selected position
        selectedPosition = index;

        // Enable the Create button
        createMatchButton.setEnabled(true);
    }
}