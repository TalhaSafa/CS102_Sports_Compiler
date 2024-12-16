package com.example.sportscompiler.AdditionalClasses;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Player
{
    private Positions position;
    private String userID;
    private double rating;
    private TeamType team;
    private boolean isAdmin;
    private String matchID;
    private String name;

    public Player(){}

    public Player(String userID1, double rating1, TeamType team1, Positions position1, boolean isAdmin1, String matchID1, String name)
    {
        userID = userID1;
        rating = rating1;
        team = team1;
        position = position1;
        isAdmin = isAdmin1;
        matchID = matchID1;

        this.name = name;
    }
    public Positions getPosition() {
        return position;
    }

    public void setPosition(Positions position) {
        this.position = position;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void getRating(RatingCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("averageRating")) {
                double updatedRating = documentSnapshot.getDouble("averageRating");
                callback.onRatingFetched(updatedRating); // Pass the rating to the callback
            } else {
                callback.onRatingFetched(rating); // Pass a default value or handle the error
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            callback.onRatingFetched(rating); // Handle failure case
        });
    }

    public void getUpdatedRating(RatingCallback callback)
    {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("averageRating")) {
                double updatedRating = documentSnapshot.getDouble("averageRating");
                callback.onRatingFetched(updatedRating); // Pass the rating to the callback
            } else {
                callback.onRatingFetched(rating); // Pass a default value or handle the error
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            callback.onRatingFetched(rating); // Handle failure case
        });
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public TeamType getTeam() {
        return team;
    }

    public void setTeam(TeamType team) {
        this.team = team;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
