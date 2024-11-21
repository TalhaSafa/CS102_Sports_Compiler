package com.example.sportscompiler.AdditionalClasses;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.Executor;

public class firestoreUser {

    private FirebaseFirestore firestore;
    private FirebaseAuth Fauth;
    private String userID;

    public void updateInfo(User user, FirestoreCallback<User> callback) {
        Fauth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = Fauth.getCurrentUser().getUid();
        user.setUserID(userID);

        DocumentReference documentReference = firestore.collection("users").document(userID);

        // Add the snapshot listener
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("FirestoreUser", "Error fetching user data", error);
                    callback.onError(error);
                    return;
                }

                if (value != null && value.exists()) {
                    user.setName(value.getString("name"));
                    user.setDepartment(value.getString("department"));
                    user.setBirthDate(value.getString("birthDate"));
                    callback.onSuccess(user); // Notify callback of the updated user
                } else {
                    Log.e("FirestoreUser", "No data found for user");
                    callback.onError(new Exception("No data found"));
                }
            }
        });
    }

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}

