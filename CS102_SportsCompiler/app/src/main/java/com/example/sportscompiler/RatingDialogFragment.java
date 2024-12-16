package com.example.sportscompiler;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sportscompiler.AdditionalClasses.Team;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingDialogFragment extends DialogFragment {

    private TextView nameTextView, currentRatingTextView;
    private Spinner ratingChooser;
    private Button submitButton;


    public RatingDialogFragment() {
        // Required empty public constructor
    }


    public static RatingDialogFragment newInstance(String param1, String param2) {
        RatingDialogFragment fragment = new RatingDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rating_dialog, container, false);
        nameTextView = rootView.findViewById(R.id.nameTextView);
        currentRatingTextView = rootView.findViewById(R.id.currentRatingTextView);
        ratingChooser = rootView.findViewById(R.id.ratingChooser);
        submitButton = rootView.findViewById(R.id.submitButton);

        return inflater.inflate(R.layout.fragment_rating_dialog, container, false);
    }
}