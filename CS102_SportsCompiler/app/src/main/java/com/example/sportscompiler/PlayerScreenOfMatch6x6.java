package com.example.sportscompiler;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportscompiler.AdditionalClasses.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlayerScreenOfMatch6x6 extends AppCompatActivity {

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_screen_of_match6x6);
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
                        Toast.makeText(PlayerScreenOfMatch6x6.this, "Rated Player", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlayerScreenOfMatch6x6.this, "Could not rate player", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PlayerScreenOfMatch6x6.this, "Could not access player", Toast.LENGTH_SHORT).show();

            }
        });
    }
}