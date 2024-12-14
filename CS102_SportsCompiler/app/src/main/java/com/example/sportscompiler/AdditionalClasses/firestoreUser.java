package com.example.sportscompiler.AdditionalClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class firestoreUser {

    private FirebaseFirestore firestore;
    private FirebaseAuth Fauth;
    private String userID;

    public void updateInfo(User user, FirestoreCallback<User> callback) {
        Fauth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = Fauth.getCurrentUser().getUid();

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
                    User updatedUser;
                    updatedUser = value.toObject(User.class);
                    callback.onSuccess(updatedUser); // Notify callback of the updated user
                } else {
                    Log.e("FirestoreUser", "No data found for user");
                    callback.onError(new Exception("No data found"));
                }
            }
        });
    }
    private void setProfilePicture (Context context, Uri imageURI, ImageView imageView){

    }

   private Bitmap uriToBitmap(Uri uri, Context context) throws IOException {

        return ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), uri));
    }

   private  String encodeImageToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray,Base64.DEFAULT);
    }

    public Bitmap decodeBase64ToImage(String base64String){
        byte[] decodedBytes = Base64.decode(base64String,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void saveImageFireStore(String base64Image, Context context){
        Map<String,Object> data = new HashMap<>();
        data.put("profilePicture", base64Image);

        if(context != null) {
            firestore.collection("users").document(userID).set(data,
                            SetOptions.merge()).addOnSuccessListener(aVoid -> Toast.makeText(context, "Image saved to Firestore", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        else {
            return;
        }
    }

    public void saveImage(Uri uri, Context context) {
        try {
            if (uri == null) {
                Log.e("FirestoreUser", "Image URI is null");
                Toast.makeText(context, "Invalid image URI", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap bitmap = uriToBitmap(uri, context);
            if (bitmap == null) {
                Log.e("FirestoreUser", "Failed to convert URI to Bitmap");
                Toast.makeText(context, "Error processing image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Log bitmap details
            Log.d("FirestoreUser", "Bitmap dimensions: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            String base64Image = encodeImageToBase64(bitmap);
            saveImageFireStore(base64Image, context);

        } catch (IOException e) {
            Toast.makeText(context, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("FirestoreUser", "IOException in saveImage", e);
        }
    }

    public Uri saveBitmapToFile(Context context, Bitmap bitmap) {
        try {
            File file = new File(context.getCacheDir(), "temp_image.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getMatches(FirestoreCallback<List<Match>> callback) {
        firestore = FirebaseFirestore.getInstance(); // Ensure firestore is initialized.
        List<Match> matches = new ArrayList<>();
        firestore.collection("matches5").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();

                    Match match = new Match();
                    match.setNotes((String) data.get("notes"));
                    String fieldVal = (String)data.get("field");
                    MatchFields enumar = MatchFields.fromString(fieldVal);
                    match.setField(enumar);
                    match.setAdminName((String) data.get("adminName"));
                    match.setAdminID((String) data.get("adminID"));
                    match.setMatchName((String) data.get("matchName"));
                    match.setDate((Timestamp) data.get("date"));
                    match.setMatchID((String) data.get("matchID"));

                    // Deserialize playersA and playersB manually
                    Map<String, Object> playersAData = (Map<String, Object>) data.get("playersA");
                    Map<String, Player> playersA = new HashMap<>();
                    for (Map.Entry<String, Object> entry : playersAData.entrySet()) {
                        //Map<String, Object> playerData = (Map<String, Object>) entry.getValue();
                        Player player = new Player();
                        playersA.put(entry.getKey(), player);
                    }

                    match.setPlayersA(playersA);
                    // Repeat for playersB
                    matches.add(match);
                }
                callback.onSuccess(matches); // Return matches through callback.
            } else {
                callback.onError(task.getException());
            }
        });
        firestore.collection("matches6").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();

                    Match match = new Match();
                    match.setNotes((String) data.get("notes"));
                    String fieldVal = (String)data.get("field");
                    MatchFields enumar = MatchFields.fromString(fieldVal);
                    match.setField(enumar);
                    match.setAdminName((String) data.get("adminName"));
                    match.setAdminID((String) data.get("adminID"));
                    match.setMatchName((String) data.get("matchName"));
                    match.setDate((Timestamp) data.get("date"));

                    // Deserialize playersA and playersB manually
                    Map<String, Object> playersAData = (Map<String, Object>) data.get("playersA");
                    Map<String, Player> playersA = new HashMap<>();
                    for (Map.Entry<String, Object> entry : playersAData.entrySet()) {
                        //Map<String, Object> playerData = (Map<String, Object>) entry.getValue();
                        Player player = new Player();
                        playersA.put(entry.getKey(), player);
                    }

                    match.setPlayersA(playersA);
                    // Repeat for playersB
                    matches.add(match);
                }
                callback.onSuccess(matches); // Return matches through callback.
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public void getMatchesForUser(FirestoreCallback<List<Match>> callback) {
        firestore = FirebaseFirestore.getInstance(); // Ensure firestore is initialized.
        List<Match> matches = new ArrayList<>();
        firestore.collection("matches5").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();

                    Match match = new Match();
                    match.setNotes((String) data.get("notes"));
                    String fieldVal = (String)data.get("field");
                    MatchFields enumar = MatchFields.fromString(fieldVal);
                    match.setField(enumar);
                    match.setAdminName((String) data.get("adminName"));
                    match.setAdminID((String) data.get("adminID"));
                    match.setMatchName((String) data.get("matchName"));
                    match.setDate((Timestamp) data.get("date"));

                    // Deserialize playersA and playersB manually
                    Map<String, Object> playersAData = (Map<String, Object>) data.get("playersA");
                    Map<String, Player> playersA = new HashMap<>();
                    for (Map.Entry<String, Object> entry : playersAData.entrySet()) {
                        //Map<String, Object> playerData = (Map<String, Object>) entry.getValue();
                        Player player = new Player();
                        playersA.put(entry.getKey(), player);
                    }

                    match.setPlayersA(playersA);
                    // Repeat for playersB
                    matches.add(match);
                }
                callback.onSuccess(matches); // Return matches through callback.
            } else {
                callback.onError(task.getException());
            }
        });
        firestore.collection("matches6").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();

                    Match match = new Match();
                    match.setNotes((String) data.get("notes"));
                    String fieldVal = (String)data.get("field");
                    MatchFields enumar = MatchFields.fromString(fieldVal);
                    match.setField(enumar);
                    match.setAdminName((String) data.get("adminName"));
                    match.setAdminID((String) data.get("adminID"));
                    match.setMatchName((String) data.get("matchName"));
                    match.setDate((Timestamp) data.get("date"));

                    // Deserialize playersA and playersB manually
                    Map<String, Object> playersAData = (Map<String, Object>) data.get("playersA");
                    Map<String, Player> playersA = new HashMap<>();
                    for (Map.Entry<String, Object> entry : playersAData.entrySet()) {
                        //Map<String, Object> playerData = (Map<String, Object>) entry.getValue();
                        Player player = new Player();
                        playersA.put(entry.getKey(), player);
                    }

                    match.setPlayersA(playersA);
                    // Repeat for playersB
                    matches.add(match);
                }
                callback.onSuccess(matches); // Return matches through callback.
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public String getUserID()
    {
        Fauth = FirebaseAuth.getInstance();
        userID = Fauth.getUid();
        return userID;
    }

    public void getApplicationsForThatMatch(){

    }
    public void updateInfoQuick(User user, FirestoreCallback<User> callback) {
        Fauth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = Fauth.getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("users").document(userID);

        documentReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User updatedUser = documentSnapshot.toObject(User.class);
                        callback.onSuccess(updatedUser); // Notify callback of the updated user
                    } else {
                        Log.e("FirestoreUser", "No data found for user");
                        callback.onError(new Exception("No data found"));
                    }
                })
                .addOnFailureListener(error -> {
                    Log.e("FirestoreUser", "Error fetching user data", error);
                    callback.onError(error);
                });
    }



    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}

