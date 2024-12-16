package com.example.sportscompiler;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportscompiler.AdditionalClasses.Team;
import com.example.sportscompiler.AdditionalClasses.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingDialogFragment extends DialogFragment {

    private FirebaseFirestore firestore;
    private String ratingS, playerName, currentRating, userID;
    private double rating;
    private TextView nameTextView, currentRatingTextView;
    private Spinner ratingChooser;
    private Button submitButton, cancelButton;


    public RatingDialogFragment() {
        // Required empty public constructor
    }


    public static RatingDialogFragment newInstance(String playerName, String currentRating, String userID) {
        RatingDialogFragment fragment = new RatingDialogFragment();
        Bundle args = new Bundle();
        args.putString("playerName", playerName);
        args.putString("currentRating", currentRating);
        args.putString("userID", userID);
        fragment.setArguments(args);
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
        cancelButton = rootView.findViewById(R.id.cancelButton);
        ratingS = ratingChooser.getSelectedItem().toString();

        if(getArguments() != null)
        {
            playerName = getArguments().getString("playerName");
            currentRating = getArguments().getString("currentRating");
            userID = getArguments().getString("userID");
        }

        List<String> ratingCount = new ArrayList<>();

        for(int i = 0; i < 11; i++)
        {
            ratingCount.add(String.valueOf(i));
        }

        if(!ratingS.equalsIgnoreCase("Item1"))
        {
            rating = Double.parseDouble(ratingS);
        }

        ArrayAdapter<String> ratingCountAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ratingCount);
        ratingCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingChooser.setAdapter(ratingCountAdapter);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                pushRating(rating, userID);
            }
        });

        return inflater.inflate(R.layout.fragment_rating_dialog, container, false);
    }
    private void pushRating(double rating, String userID)
    {
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                double totalRating = user.getRatingNumber() * user.getAverageRating();
                totalRating += rating;
                user.addRatingNumber();
                user.setAverageRating(totalRating / user.getRatingNumber());

                firestore.collection("users").document(userID).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(requireContext(), "Rated Player", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Could not rate player", Toast.LENGTH_SHORT).show();
                        double totalRating = user.getRatingNumber() * user.getAverageRating();
                        totalRating -= rating;
                        user.decreaseRatingNumber();
                        user.setAverageRating(totalRating / user.getRatingNumber());

                    }

                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Could not access player", Toast.LENGTH_SHORT).show();

            }
        });
    }
}